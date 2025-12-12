package com.app.milobackend.services;

import com.app.milobackend.dtos.FilterDTO;
import com.app.milobackend.dtos.MailDTO;
import com.app.milobackend.filter.Criteria;
import com.app.milobackend.filter.CriteriaFactory;
import com.app.milobackend.mappers.MailMapperImpl;
import com.app.milobackend.models.ClientUser;
import com.app.milobackend.models.Mail;
import com.app.milobackend.repositories.AttachmentRepository;
import com.app.milobackend.repositories.FolderRepo;
import com.app.milobackend.repositories.MailRepo;
import com.app.milobackend.repositories.UserRepo;
import com.app.milobackend.strategies.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.event.EventListener;
import org.springframework.data.domain.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

@Service
public class MailService {

    @Autowired
    private MailRepo mailRepo;

    @Autowired
    private AttachmentRepository attachmentRepo;

    @Autowired
    private AttachmentService attachmentService;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FolderRepo folderRepo;

    @Autowired
    private MailMapperImpl mailMapper;

    @Autowired
    @Lazy // <--- CRITICAL: Prevents application crash on startup
    private MailService self;

    public String getCurrentUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null; // Or throw an exception
    }

    @EventListener(ApplicationReadyEvent.class)
    public void init() {
        System.out.println("Warming up cache...");
        try {
            self.GetAllMails(); // This triggers the DB fetch and stores it in Redis/Memory
        } catch (Exception e) {
            System.err.println("Database not ready yet, skipping cache warm-up.");
            System.err.println(e.getMessage());
            for (StackTraceElement ele : e.getStackTrace()) {
                System.err.println(ele.toString());
            }
        }
    }

    // 1. READ (The Cache)
    // The result of this method is stored in Redis under the key "user_mails"
    // Note: You should ideally cache by User ID (e.g., "mails_user_1")
    @Cacheable(value = "mails", key = "'all_mails'")
    @Transactional(readOnly = true)
    public List<Mail> GetAllMails() {
        System.out.println("Fetching from Database..."); // You will only see this once!
        List<Mail> mails =  mailRepo.findAllWithDetails();

        for (Mail m : mails) {
            if (m.getReceivers() != null) {
                m.setReceivers(new HashSet<>(m.getReceivers()));
            }
            if (m.getAttachments() != null) {
                m.setAttachments(new HashSet<>(m.getAttachments()));
            }
            // If you have other relationships (like sender), they are usually fine
            // unless they are also proxy objects causing issues.
        }

        return mails;
    }

    public void deleteMail(Long id) {
        if (mailRepo.findById(id).isPresent()) {
            mailRepo.deleteById(id);
        }
    }

    public Mail GetMailById(long id) {
        return mailRepo.findById(id).orElse(null);
    }

    @CacheEvict(value = "mails", allEntries = true)
    public Mail UpdateMail(Mail mail) {
        return mailRepo.save(mail);
    }

    @CacheEvict(value = "mails", allEntries = true)
    @Transactional
    public void saveMail(MailDTO mailDTO) throws RuntimeException {
        String currentEmail = getCurrentUserEmail();
        ClientUser sender = userRepo.findByEmail(currentEmail);

        Mail mail = mailMapper.toEntity(mailDTO);

        mail.setSender(sender);

        mailRepo.save(mail);
    }


    @Cacheable(value = "mails", key = "#folderName + '_' + #root.target.getCurrentUserEmail() + '_' + #pageNumber")
    public Page<MailDTO> getMailsByFolder(String folderName, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("sentAt").descending());
        String userEmail = getCurrentUserEmail();

        if (userEmail == null) {
            throw new RuntimeException("User not authenticated");
        }

        Page<Mail> mailPage;
        // Logic breakdown based on your requirements:
        if ("starred".equalsIgnoreCase(folderName)) {
            // "check both the Mail sender field and recievers" + starred
            mailPage = mailRepo.findStarredMailsForUser(userEmail, pageable);
        }
        else if ("inbox".equalsIgnoreCase(folderName)) {
            // "check for the mails receivers field"
            ClientUser receiver = userRepo.findByEmail(userEmail);
            mailPage = convertListToPage(receiver.getReceivedMails(), pageNumber, pageSize);
//            mailPage = mailRepo.findReceivedMailsByFolder(folderName, userEmail, pageable);
        }
        else if ("sent".equalsIgnoreCase(folderName) || "draft".equalsIgnoreCase(folderName)) {
            // "check for the mails sender field"
            ClientUser sender = userRepo.findByEmail(userEmail);
            mailPage = convertListToPage(sender.getSentMails(), pageNumber, pageSize);
//            mailPage = mailRepo.findSentMailsByFolder(folderName, userEmail, pageable);
        }
        else {
            // "something else... check for both the sender field and receivers field"
            mailPage = mailRepo.findMailsByFolderAndUserInvolvement(folderName, userEmail, pageable);
        }

        return mailPage.map(mail -> mailMapper.toDTO(mail));
    }

    public Page<Mail> getSortedMails(String sortBy, String folderName, int pageNumber, int pageSize) {
        SortWorker sortworker = new SortWorker();
        switch(sortBy.toLowerCase()){
            case "subject":
                sortworker.setStrategy(new SortBySubject());
                break;
            case "date":
                sortworker.setStrategy(new SortByDate());
                break;
            case "priority":
                sortworker.setStrategy(new SortByPriority());
                break;
            case "sender":
                sortworker.setStrategy(new SortBySender());
                break;
            case "receiver":
                sortworker.setStrategy(new SortByReceiver());
                break;
            case "body":
                sortworker.setStrategy(new SortByBody());
                break;
            case "attachments":
                sortworker.setStrategy(new SortByAttachment());
                break;
            default:
                throw new IllegalArgumentException("Invalid sort by");
        }
        List<Mail> mailsToSort = new ArrayList<>(self.GetAllMails());
        for (Mail mail : mailsToSort) {
            System.out.println(mail.toString());
        }
        Long startTime = System.currentTimeMillis();
        List<Mail> filtered;
        if ("starred".equalsIgnoreCase(folderName)) {
            filtered = mailsToSort.stream()
                    .filter(mail -> mail != null && Boolean.TRUE.equals(mail.isStarred()))
                    .toList();
        } else {
            filtered = mailsToSort.stream()
                    .filter(mail -> mail != null && mail.getFolder() != null && folderName.equals(mail.getFolder().getName()))
                    .toList();
        }
        List<Mail> sortedMails =  sortworker.sort(filtered);
        Long endTime = System.currentTimeMillis();
        System.out.println("Time taken in sort: " + (endTime - startTime) + " ms");
        return convertListToPage(sortedMails, pageNumber, pageSize);
    }

    public Page<Mail> Filter(FilterDTO request, int pageNumber, int pageSize) {
        String word = request.getWord();
        List<String> selectedCriteria = request.getCriteria();
        if(selectedCriteria == null || selectedCriteria.isEmpty()){
            selectedCriteria = CriteriaFactory.allCriteriaNames();
        }
        List<Mail> sourceMails = self.GetAllMails();

        List<Mail> filteredMails = new ArrayList<>();
        for(String name : selectedCriteria){
            Criteria criteria = CriteriaFactory.create(name, word);
            if(criteria != null){
                filteredMails.addAll(criteria.filter(sourceMails));
            }
        }
        return convertListToPage(filteredMails.stream().distinct().toList(), pageNumber, pageSize);
    }


    @CacheEvict(value = "mails", allEntries = true)
    public void toggleStarredMail(Long mailId) {
        Mail mail = mailRepo.findById(mailId).orElse(null);
        if (mail == null) {
            System.out.println("mail to toggle star: not found for id=" + mailId);
            return;
        }
        System.out.println("mail to toggle star (before): " + mail);
        mail.setStarred(!mail.isStarred());
        mailRepo.save(mail);
        System.out.println("mail to toggle star (after): " + mail);
    }

    private <T> Page<T> convertListToPage(List<T> list, int pageNumber, int pageSize) {
        // 1. Create Pageable
        Pageable pageable = PageRequest.of(pageNumber, pageSize);

        // 2. Calculate Start Item
        int start = (int) pageable.getOffset();

        // 3. Calculate End Item (Handle out of bounds)
        int end = Math.min((start + pageable.getPageSize()), list.size());

        // 4. Handle "Page is empty" case
        if (start > list.size()) {
            return new PageImpl<>(new ArrayList<>(), pageable, list.size());
        }

        // 5. Create Sublist
        List<T> content = list.subList(start, end);

        // 6. Return Page
        return new PageImpl<>(content, pageable, list.size());
    }
}

package com.app.milobackend.services;

import com.app.milobackend.dtos.FilterDTO;
import com.app.milobackend.dtos.MailDTO;
import com.app.milobackend.filter.Criteria;
import com.app.milobackend.filter.CriteriaFactory;
import com.app.milobackend.mappers.MailMapperImpl;
import com.app.milobackend.models.*;
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

import java.util.*;

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
    @Lazy
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


    @Transactional(readOnly = true)
    @Cacheable(value = "mails", key = "'all_mails'")
    public List<Mail> GetAllMails() {
        System.out.println("Fetching from Database..."); // You will only see this once!
        List<Mail> mails =  mailRepo.findAllWithDetails();

        for (Mail m : mails) {
            if (m.getAttachments() != null) {
                m.setAttachments(new HashSet<>(m.getAttachments()));
            }
        }

        return mails;
    }

    @Transactional
    @CacheEvict(value = "mails", allEntries = true)
    public void deleteMail(Long id) {
        if (mailRepo.findById(id).isPresent()) {
            mailRepo.deleteById(id);
        }
    }

    public Mail GetMailById(long id) {
        return mailRepo.findById(id).orElse(null);
    }

    @Transactional
    @CacheEvict(value = "mails", allEntries = true)
    public void updateMail(MailDTO mailDTO) {
        if (mailDTO.getId() == null) {
            throw new RuntimeException("Cannot update incomingMail without ID");
//            this.saveMail(mailDTO);
        }

        System.out.println("Updating incomingMail...");
        System.out.println("mailDTO coming with update: " + mailDTO.toString());

        Mail incomingMail = mailMapper.toEntity(mailDTO);
        System.out.println("incomingMail after update (Mail object): " + incomingMail.toString());

        Mail existingMail = mailRepo.findById(incomingMail.getId())
                .orElseThrow(() -> new RuntimeException("Mail not found with ID: " + mailDTO.getId()));

        existingMail.update(incomingMail);
        mailRepo.save(existingMail);
    }


    @Transactional
    @CacheEvict(value = "mails", allEntries = true)
    public void saveMail(MailDTO mailDTO) throws RuntimeException {
        String currentEmail = getCurrentUserEmail();
        ClientUser sender = userRepo.findByEmail(currentEmail);

        // Step 1: Create the sender's copy (goes to their sent/drafts folder)
        Mail senderMail = mailMapper.toEntity(mailDTO);
        senderMail.setSender(sender);
        sender.addSentMail(senderMail);
        
        // The folder is already set by the mapper based on mailDTO.getFolder() 
        // (e.g., "sent" for sending, "drafts" for saving draft)
        senderMail.setId(null);
        mailRepo.save(senderMail);

        // Step 2: Process the queue of receivers - each gets their own copy in their inbox
        // Only create receiver copies if this is NOT a draft (folder != "drafts")
        if (!"drafts".equalsIgnoreCase(mailDTO.getFolder())) {
            Queue<String> receiverQueue = mailDTO.getReceiverEmails();
            
            while (receiverQueue != null && !receiverQueue.isEmpty()) {
                String receiverEmail = receiverQueue.remove();
                ClientUser receiver = userRepo.findByEmail(receiverEmail);
                
                if (receiver == null) {
                    throw new RuntimeException("Receiver not found: " + receiverEmail);
                }
                
                // Create a copy for this receiver using the copy constructor
                Mail receiverMail = new Mail(senderMail, receiver);
                receiverMail.setRead(false); // New mail is unread for receiver
                
                // Add to receiver's inbox folder
                Folder receiverInbox = folderRepo.findByNameAndUserEmail("inbox", receiver.getEmail());
                if (receiverInbox != null) {
                    receiverInbox.addMail(receiverMail);
                    receiverMail.setFolder(receiverInbox);
                }
                
                receiver.addReceivedMail(receiverMail);
                mailRepo.save(receiverMail);
            }
        }
    }


    @Transactional(readOnly = true)
    @Cacheable(value = "mails", key = "#folderName + '_' + #root.target.getCurrentUserEmail() + '_' + #pageNumber + '_' + #pageSize")
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
            // "check for the mails receivers field" - only mails in inbox folder where user is receiver
            mailPage = mailRepo.findReceivedMailsByFolder("inbox", userEmail, pageable);
        }
        else if ("sent".equalsIgnoreCase(folderName) || "drafts".equalsIgnoreCase(folderName)) {
            // "check for the mails sender field" - only mails in sent/drafts folder where user is sender
            mailPage = mailRepo.findSentMailsByFolder(folderName, userEmail, pageable);
//            mailPage = mailRepo.findSentMailsByFolder(folderName, userEmail, pageable);
        }
        else {
            // "something else... check for both the sender field and receivers field"
            mailPage = mailRepo.findMailsByFolderAndUserInvolvement(folderName, userEmail, pageable);
        }

        return mailPage.map(mail -> mailMapper.toDTO(mail));
    }


    @Transactional
    @CacheEvict(value = "mails", allEntries = true)
    public void moveMailsToFolder(Map<String, Object> mailIds_folder) {
        String folderName = (String) mailIds_folder.get("folder");
        List<Long> ids =  (List<Long>) mailIds_folder.get("ids");
        String userEmail = getCurrentUserEmail();
        List<Mail> mails = mailRepo.findByIdIn(ids);
        Folder folder = folderRepo.findByNameAndUserEmail(folderName, userEmail);
        for (Mail mail : mails) {
            mail.setFolder(folder);
            mailRepo.save(mail);

            folder.addMail(mail);
            folderRepo.save(folder);
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "mails", key = "#sortBy + '_' + #folderName + '_' + #pageNumber + '_' + #pageSize + '_'+ #root.target.getCurrentUserEmail()")
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

    @Transactional(readOnly = true)
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


    @Transactional
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

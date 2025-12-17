package com.app.milobackend.services;

import com.app.milobackend.commands.ActionFactory;
import com.app.milobackend.dtos.FilterDTO;
import com.app.milobackend.dtos.SearchDTO;
import com.app.milobackend.dtos.MailDTO;
import com.app.milobackend.filter.Criteria;
import com.app.milobackend.filter.CriteriaFactory;
import com.app.milobackend.mappers.MailMapperImpl;
import com.app.milobackend.models.*;
import com.app.milobackend.repositories.*;
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
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;

@Service
public class MailService {

    @Autowired
    private MailRepo mailRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FolderRepo folderRepo;

    @Autowired
    private MailMapperImpl mailMapper;

    @Autowired
    private FilterRuleRepo filterRuleRepo;

    @Autowired
    @Lazy
    private MailService self;

    @Autowired
    private ActionFactory actionFactory;

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
    public void GetAllMails() {
        System.out.println("Fetching from Database..."); // You will only see this once!
        List<Mail> mails = mailRepo.findAllWithDetails();

        for (Mail m : mails) {
            if (m.getAttachments() != null) {
                m.setAttachments(new HashSet<>(m.getAttachments()));
            }
        }
    }

    @Transactional
    @CacheEvict(value = "mails", allEntries = true)
    public void deleteMail(Long id) {
        if (mailRepo.findById(id).isPresent()) {
            mailRepo.deleteById(id);
        }
    }

    @Transactional
    @CacheEvict(value = "mails", allEntries = true)
    public void updateMail(MailDTO mailDTO, List<MultipartFile> files) throws IOException {
        if (mailDTO.getId() == null) {
            throw new RuntimeException("Cannot update incomingMail without ID");
            // this.saveMail(mailDTO);
        }

        System.out.println("Updating incomingMail...");
        System.out.println("mailDTO coming with update: " + mailDTO.toString());

        Mail incomingMail = mailMapper.toEntity(mailDTO, files);
        System.out.println("incomingMail after update (Mail object): " + incomingMail.toString());

        Mail existingMail = mailRepo.findById(incomingMail.getId())
                .orElseThrow(() -> new RuntimeException("Mail not found with ID: " + mailDTO.getId()));

        existingMail.update(incomingMail);
        mailRepo.save(existingMail);
    }

    @Transactional
    @CacheEvict(value = "mails", allEntries = true)
    public void saveMail(MailDTO mailDTO, List<MultipartFile> files) throws RuntimeException, IOException {
        System.err.println("=== Starting saveMail ===");
        String currentEmail = getCurrentUserEmail();
        System.err.println("Current user email: " + currentEmail);

        ClientUser sender = userRepo.findByEmail(currentEmail);
        if (sender == null) {
            throw new RuntimeException("Sender not found: " + currentEmail);
        }
        System.out.println("Sender found: " + sender.getName());

        // Step 1: Create the sender's copy (goes to their sent/drafts folder) using
        // Prototype pattern
        Mail mappedMail = mailMapper.toEntity(mailDTO, files);
        Mail senderMail = mappedMail.clone();
        senderMail.setFolder(mappedMail.getFolder());

        System.err.println("Mail entity created, folder: "
                + (senderMail.getFolder() != null ? senderMail.getFolder().getName() : "NULL"));
        System.err.println(
                "Attachments count: " + (senderMail.getAttachments() != null ? senderMail.getAttachments().size() : 0));

        senderMail.setSender(sender);
        sender.addSentMail(senderMail);

        // The folder is already set by the mapper based on mailDTO.getFolder()
        // (e.g., "sent" for sending, "drafts" for saving draft)
        if (mappedMail.getId() == 0)
            senderMail.setId(null);
        else
            senderMail.setId(mappedMail.getId());

        System.out.println("About to save sender mail...");
        Mail savedMail = mailRepo.save(senderMail);
        System.out.println("Sender mail saved with ID: " + savedMail.getId());

        // Step 2: Process the queue of receivers - each gets their own copy in their
        // inbox
        // Only create receiver copies if this is NOT a draft (folder != "drafts")
        if (!"drafts".equalsIgnoreCase(mailDTO.getFolder())) {
            Queue<String> receiverQueue = mailDTO.getReceiverEmails();

            while (receiverQueue != null && !receiverQueue.isEmpty()) {
                String receiverEmail = receiverQueue.remove();
                ClientUser receiver = userRepo.findByEmail(receiverEmail);

                if (receiver == null) {
                    throw new RuntimeException("Receiver not found: " + receiverEmail);
                }

                // Create a copy for this receiver using the Prototype pattern
                Mail receiverMail = senderMail.cloneWithReceiver(receiver);
                receiverMail.setRead(false); // New mail is unread for receiver

                // Load filter rules for this RECEIVER (not sender!)
                List<FilterRule> filterRules = filterRuleRepo.findByUserEmail(receiverEmail);

                // Apply matching filter rules - wrapped in try-catch to prevent filter bugs
                // from breaking mail send
                try {
                    for (FilterRule rule : filterRules) {
                        if (rule.check(receiverMail)) {
                            rule.apply(receiverMail, actionFactory);
                            System.out.println("Filter rule " + rule.getId() + " matched and applied");
                            break;
                        }
                    }
                } catch (Exception e) {
                    System.err.println("Error applying filter rules: " + e.getMessage());
                    e.printStackTrace();
                    // Continue without filtering - don't fail mail delivery
                }

                // Always ensure mail has a folder - default to inbox if not set by filter
                // action
                if (receiverMail.getFolder() == null) {
                    Folder receiverInbox = folderRepo.findByNameAndUserEmail("inbox", receiver.getEmail());
                    if (receiverInbox != null) {
                        receiverInbox.addMail(receiverMail);
                        receiverMail.setFolder(receiverInbox);
                    }
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
            // check both the Mail sender field and receivers + starred
            mailPage = mailRepo.findStarredMailsForUser(userEmail, pageable);
        } else if ("inbox".equalsIgnoreCase(folderName)) {
            // "check for the mails receivers field" - only mails in inbox folder where user
            // is receiver
            mailPage = mailRepo.findReceivedMailsByFolder("inbox", userEmail, pageable);
        } else if ("sent".equalsIgnoreCase(folderName) || "drafts".equalsIgnoreCase(folderName)) {
            // "check for the mails sender field" - only mails in sent/drafts folder where
            // user is sender
            mailPage = mailRepo.findSentMailsByFolder(folderName, userEmail, pageable);
        } else {
            // "something else... check for both the sender field and receivers field"
            mailPage = mailRepo.findMailsByFolderAndUserInvolvement(folderName, userEmail, pageable);
        }

        return mailPage.map(mailMapper::toDTO);
    }

    @Transactional
    @CacheEvict(value = "mails", allEntries = true)
    public void moveMailsToFolder(Map<String, Object> mailIds_folder) {
        String folderName = (String) mailIds_folder.get("folder");
        List<Long> ids = (List<Long>) mailIds_folder.get("ids");
        String userEmail = getCurrentUserEmail();
        List<Mail> mails = mailRepo.findByIdIn(ids);
        Folder folder = folderRepo.findByNameAndUserEmail(folderName, userEmail);
        for (Mail mail : mails) {
            if (folderName.equalsIgnoreCase("trash")) {
                mail.setTrashedAt(LocalDateTime.now(ZoneId.of("Africa/Cairo")));
            } else {
                mail.setTrashedAt(null);
            }
            mail.setFolder(folder);
            mailRepo.save(mail);

            folder.addMail(mail);
            folderRepo.save(folder);
        }
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "mails", key = "#sortBy + '_' + #folderName + '_' + #pageNumber + '_' + #pageSize + '_'+ #root.target.getCurrentUserEmail()")
    public Page<MailDTO> getSortedMails(String sortBy, String folderName, int pageNumber, int pageSize) {
        SortWorker sortworker = new SortWorker();
        switch (sortBy.toLowerCase()) {
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
        Pageable pageable = PageRequest.of(pageNumber, pageSize);
        List<Mail> mailsToSort;
        if (folderName.equalsIgnoreCase("inbox")) {
            mailsToSort = mailRepo.findReceivedMailsByFolder(folderName, getCurrentUserEmail(), pageable).stream()
                    .toList();
        } else if (folderName.equalsIgnoreCase("sent")) {
            mailsToSort = mailRepo.findSentMailsByFolder(folderName, getCurrentUserEmail(), pageable).stream().toList();
        } else {
            mailsToSort = mailRepo.findMailsByFolderAndUserInvolvement(folderName, getCurrentUserEmail(), pageable)
                    .stream().toList();
        }

        for (Mail mail : mailsToSort) {
            System.out.println(mail.toString());
        }
        Long startTime = System.currentTimeMillis();
        List<Mail> filtered;
        if ("starred".equalsIgnoreCase(folderName)) {
            filtered = mailsToSort.stream()
                    .filter(mail -> mail != null && mail.isStarred())
                    .toList();
        } else {
            filtered = mailsToSort.stream()
                    .filter(mail -> mail != null && mail.getFolder() != null
                            && folderName.equalsIgnoreCase(mail.getFolder().getName()))
                    .toList();
        }
        List<Mail> sortedMails = sortworker.sort(filtered);
        Long endTime = System.currentTimeMillis();
        System.out.println("Time taken in sort: " + (endTime - startTime) + " ms");
        Page<Mail> mails = convertListToPage(sortedMails, pageNumber, pageSize);
        return mails.map(mailMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<MailDTO> Search(SearchDTO request, int pageNumber, int pageSize) {
        String word = request.getWord();
        List<String> selectedCriteria = request.getCriteria();
        if (selectedCriteria == null || selectedCriteria.isEmpty()) {
            selectedCriteria = CriteriaFactory.allCriteriaNames();
        }
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("sentAt").descending());
        List<Mail> sourceMails = mailRepo.findMailsByUserInvolvement(getCurrentUserEmail(), pageable).stream().toList();

        List<Mail> filteredMails = new ArrayList<>();
        for (String name : selectedCriteria) {
            Criteria criteria = CriteriaFactory.create(name, word);
            if (criteria != null) {
                filteredMails.addAll(criteria.filter(sourceMails));
            }
        }
        Page<Mail> mails = convertListToPage(filteredMails.stream().distinct().toList(), pageNumber, pageSize);
        return mails.map(mailMapper::toDTO);
    }

    @Transactional(readOnly = true)
    public Page<MailDTO> Filter(FilterDTO request, int pageNumber, int pageSize) {

        Map<String, String> criteriaMap = request.getKeys();

        if (criteriaMap == null || criteriaMap.isEmpty()) {
            return Page.empty();
        }

        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("sentAt").descending());

        List<Mail> sourceMails = mailRepo.findMailsByUserInvolvement(
                getCurrentUserEmail(),
                pageable).getContent();

        List<Mail> filteredMails = new ArrayList<>(sourceMails);

        for (Map.Entry<String, String> entry : criteriaMap.entrySet()) {

            String criteriaName = entry.getKey();
            String criteriaValue = entry.getValue();

            if (criteriaValue == null || criteriaValue.isBlank()) {
                continue;
            }

            Criteria criteria = CriteriaFactory.create(criteriaName, criteriaValue);

            if (criteria != null) {
                filteredMails = criteria.filter(filteredMails);
            }

            if (filteredMails.isEmpty()) {
                break;
            }
        }

        Page<Mail> resultPage = convertListToPage(filteredMails, pageNumber, pageSize);

        return resultPage.map(mailMapper::toDTO);
    }

    @Transactional
    @CacheEvict(value = "mails", allEntries = true)
    public void toggleStarredMail(Long mailId) {
        Mail mail = mailRepo.findById(mailId).orElse(null);
        if (mail == null) {
            System.out.println("mail to toggle star: not found for id=" + mailId);
            return;
        }

        String userEmail = getCurrentUserEmail();
        boolean newStarredState = !mail.isStarred();

        // Find all related copies for this user (for syncing star status on self-sends)
        List<Mail> relatedCopies = mailRepo.findRelatedCopiesForUser(
                mail.getSubject(),
                mail.getBody(),
                mail.getSentAt(),
                mail.getSender().getEmail(),
                userEmail);

        // Sync starred status across all copies
        System.out.println("Syncing star status to " + relatedCopies.size() + " related copies");
        for (Mail copy : relatedCopies) {
            copy.setStarred(newStarredState);
            mailRepo.save(copy);
        }
    }

    @Transactional
    @CacheEvict(value = "mails", allEntries = true)
    public void markMailAsRead(Long mailId) {
        Mail mail = mailRepo.findById(mailId).orElse(null);
        if (mail != null) {
            mail.setRead(true);
            mailRepo.save(mail);
        }
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

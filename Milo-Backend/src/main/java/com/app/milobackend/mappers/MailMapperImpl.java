package com.app.milobackend.mappers;

import com.app.milobackend.dtos.AttachmentDTO;
import com.app.milobackend.dtos.MailDTO;
import com.app.milobackend.models.Attachment;
import com.app.milobackend.models.ClientUser;
import com.app.milobackend.models.Folder;
import com.app.milobackend.models.Mail;
import com.app.milobackend.repositories.FolderRepo;
import com.app.milobackend.repositories.UserRepo;
import com.app.milobackend.services.AttachmentService;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

@Component
public class MailMapperImpl implements MailMapper {

    final
    UserRepo userRepo;

    final
    AttachmentService attachmentService;

    final
    FolderRepo folderRepo;

    private final AttachmentMapper attachmentMapper;

    public MailMapperImpl(UserRepo userRepo, AttachmentService attachmentService, FolderRepo folderRepo, AttachmentMapper attachmentMapper) {
        this.userRepo = userRepo;
        this.attachmentService = attachmentService;
        this.folderRepo = folderRepo;
        this.attachmentMapper = attachmentMapper;
    }

    public String getCurrentUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null; // Or throw an exception
    }

    @Override
    public Mail toEntity(MailDTO mailDTO, List<MultipartFile> files) throws IOException {
        if (mailDTO == null) return null;

        Mail.MailBuilder mailBuilder = Mail.builder()
                .id(mailDTO.getId())
                .subject(mailDTO.getSubject())
                .body(mailDTO.getBody())
                .read(mailDTO.isRead())
                .active(mailDTO.isActive())
                .starred(mailDTO.isStarred())
                .hasAttachment(mailDTO.isHasAttachment())
                .priority(mailDTO.getPriority());

        // Map Sender
        if (mailDTO.getSenderEmail() != null) {
            ClientUser sender = userRepo.findByEmail(mailDTO.getSenderEmail());
            mailBuilder.sender(sender);
        }

        Mail mail = mailBuilder.build();

        // Handle attachments:
        // 1. Collect IDs of existing attachments that should be kept
//        Set<Long> existingAttachmentIds = new HashSet<>();
//        if (mailDTO.getAttachments() != null) {
//            for (var attachmentDTO : mailDTO.getAttachments()) {
//                if (attachmentDTO.getId() != null) {
//                    existingAttachmentIds.add(attachmentDTO.getId());
//                }
//            }
//        }
        // Store these IDs for MailService to use when preserving attachments
//        mail.setExistingAttachmentIds(existingAttachmentIds);

        // 2. Convert new Files to Attachment Entities
        System.err.println("####################################################################################");
        if (!(files == null || files.isEmpty()) || !(mailDTO.getAttachments() == null || mailDTO.getAttachments().isEmpty())) {
            List<Attachment> attachments = attachmentService.convertDTOsToAttachments(mailDTO.getAttachments(), files);
            for (Attachment attachment : attachments) {
                System.err.println("Attachment printed: " + attachment.toString());
                attachment.setMail(mail);
                mail.addAttachment(attachment);
            }
        }
        System.err.println("####################################################################################");

        String folderName = mailDTO.getFolder();
        String email = getCurrentUserEmail();
        if (email == null) {
            throw new RuntimeException("User not Authenticated");
        }
        Folder folder = folderRepo.findByNameAndUserEmail(folderName, email);

        if (folder != null) {
            mail.setFolder(folder);
            
            if (mailDTO.getId() == null) {
                List<Mail> folderMails = folder.getMails();
                folderMails.add(mail);
                folder.setMails(folderMails);
            }
        }
        
        // Note: Sender relationship (addSentMail) is handled in MailService.saveMail()
        // to avoid duplication and maintain single responsibility

        return mail;
    }

    @Override
    public MailDTO toDTO(Mail entity) {
        if (entity == null) return null;

        // Build the receiver emails queue first
        Queue<String> receiverEmailsQueue = new LinkedList<>();
        if (entity.getReceiver() != null) {
            receiverEmailsQueue.add(entity.getReceiver().getEmail());
        }

        MailDTO dto = MailDTO.builder()
                .id(entity.getId())
                .subject(entity.getSubject())
                .body(entity.getBody())
                .priority(entity.getPriority())
                .read(entity.isRead())
                .active(entity.isActive())
                .starred(entity.isStarred())
                .hasAttachment(entity.isHasAttachment())
                .time(entity.getSentAt().toString())
                .sender(entity.getSender().getName())
                .senderEmail(entity.getSender().getEmail())
                .receiverEmails(receiverEmailsQueue)
                .build();

        List<Attachment> attachments = entity.getAttachments().stream().toList();
        List<AttachmentDTO> attachmentDTOs = attachments.stream().map(
                (attachmentMapper::toDTO)).toList();

        dto.setAttachments(attachmentDTOs);
        dto.setFolder(entity.getFolder().getName());

        return dto;
    }
}

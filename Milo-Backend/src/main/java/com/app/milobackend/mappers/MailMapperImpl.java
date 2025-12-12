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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class MailMapperImpl implements MailMapper {

    @Autowired
    UserRepo userRepo;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    FolderRepo folderRepo;

    @Autowired
    private AttachmentMapper attachmentMapper;

    @Override
    public Mail toEntity(MailDTO mailDTO) {
        ClientUser clientSender=userRepo.findByEmail(mailDTO.getSenderEmail());
        Mail mail = Mail.builder()
                .sender(clientSender)
                .subject(mailDTO.getSubject())
                .body(mailDTO.getBody())
                .read(mailDTO.isRead())
                .active(mailDTO.isActive())
                .starred(mailDTO.isStarred())
                .hasAttachment(mailDTO.isHasAttachment())
                .priority(mailDTO.getPriority())
                .build();

        // 2. Convert Files to Attachment Entities (In Memory)
        List<Attachment> attachments = attachmentService.convertDTOsToAttachments(mailDTO.getAttachments());

        // 3. Link them together
        for (Attachment attachment : attachments) {
            attachment.setMail(mail); // Critical: Tells Attachment who its parent is
            mail.addAttachment(attachment); // Critical: Adds to the parent's list
        }

        String folderName = mailDTO.getFolder();
        Folder folder = folderRepo.findByName(folderName);

        if (folder != null) {
            mail.setFolder(folder);

            List<Mail> folderMails = folder.getMails();
            folderMails.add(mail);
            folder.setMails(folderMails);
        }
//        Mail mail = mapMailDTOtoMail(mailDTO);
        ClientUser sender= userRepo.findByEmail(mailDTO.getSenderEmail());
        if(sender == null) {
            throw new RuntimeException("Sender not found");
        }
        //btcheck el Email valid wla laa
        List<ClientUser> receivers=new ArrayList<>();

        for(String receiverEmail : mailDTO.getReceiverEmail()){
            ClientUser receiver = userRepo.findByEmail(receiverEmail);
            if (receiver == null) {
                throw new RuntimeException("Receiver not found");
            }
            else {
                receivers.add(receiver);
                receiver.addReceivedMail(mail);
            }

        }
        sender.addSentMail(mail);
        mail.setReceivers(new HashSet<>(receivers));

        return mail;
    }

    @Override
    public MailDTO toDTO(Mail entity) {
        if (entity == null) return null;

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
                .senderEmail(entity.getSender().getEmail()).build();

        // Map Receivers
        if (entity.getReceivers() != null) {
            List<String> receiverEmails = entity.getReceivers().stream()
                    .map(ClientUser::getEmail)
                    .collect(Collectors.toList());
            dto.setReceiverEmail(receiverEmails);
        }

        List<Attachment> attachments = entity.getAttachments().stream().toList();
        List<AttachmentDTO> attachmentDTOs = attachments.stream().map(
                (attachment -> attachmentMapper.toDTO(attachment))).toList();

        dto.setAttachments(attachmentDTOs);
        dto.setFolder(entity.getFolder().getName());

        return dto;
    }
}

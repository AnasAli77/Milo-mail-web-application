package com.app.milobackend.services;

import com.app.milobackend.dtos.FilterDTO;
import com.app.milobackend.dtos.MailDTO;
import com.app.milobackend.filter.Criteria;
import com.app.milobackend.filter.CriteriaFactory;
import com.app.milobackend.models.Attachment;
import com.app.milobackend.models.ClientUser;
import com.app.milobackend.models.Folder;
import com.app.milobackend.models.Mail;
import com.app.milobackend.repositories.AttachmentRepository;
//import com.app.milobackend.repositories.FolderRepo;
import com.app.milobackend.repositories.FolderRepo;
import com.app.milobackend.repositories.MailRepo;
import com.app.milobackend.repositories.UserRepo;
import com.app.milobackend.strategies.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Service
public class MailService {
    @Autowired
    MailRepo mailRepo;
    @Autowired
    AttachmentRepository attachmentRepo;
//    @Autowired
//    FolderRepo folderRepo;

    @Autowired
    private AttachmentService attachmentService;
    @Autowired
    private UserRepo userRepo;

    @Autowired
    private FolderRepo folderRepo;


    public void deleteMail (Long id)
    {
        if(mailRepo.findById(id).isPresent())
        {
            mailRepo.deleteById(id);
        }
    }
    public List<Mail> GetAllMails()
    {
        return mailRepo.findAll();
    }
    public Mail GetMailById(long id) {
        return mailRepo.findById(id).get();
    }
    public void AddMail(Mail mail) {
        mailRepo.save(mail);
    }
    public Mail UpdateMail(Mail mail) {
        return mailRepo.save(mail);
    }
    public Mail mapMailDTOtoMail(MailDTO mailDTO)
    {
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
        return mail;

    }
    public void saveMail(MailDTO mailDTO) throws RuntimeException {
        Mail mail = mapMailDTOtoMail(mailDTO);
        ClientUser sender= userRepo.findByEmail(mailDTO.getSenderEmail());
        if(sender==null){
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
        mail.setReceivers(receivers);
        mailRepo.save(mail);
    }
//    public Mail DeleteMail(long id) {
//        Mail mail = mailRepo.findById(id).get();
//        mailRepo.delete(mail);
//        return mail;
//    }

    public List<Mail> getSortedMails(String  sortBy)
    {
        SortWorker sortworker = new SortWorker();
        List<Mail> mails = GetAllMails();
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
            default:
                throw new IllegalArgumentException("Invalid sort by");

        }
        return sortworker.sort(mails);

    }
    public List<Mail> Filter(FilterDTO request){
        List<Mail> mails = GetAllMails();
        String word = request.getWord();
        List<String> selectedCriteria = request.getCriteria();
        if(selectedCriteria==null ||selectedCriteria.isEmpty()){
            selectedCriteria = CriteriaFactory.allCriteriaNames();
        }
        List<Mail> filteredMails = new ArrayList<>();
        for(String name : selectedCriteria){
            Criteria criteria=CriteriaFactory.create(name,word);
            if(criteria!=null){
                filteredMails.addAll(criteria.filter(mails));
            }
        }
        return filteredMails.stream().distinct().toList();
    }

}

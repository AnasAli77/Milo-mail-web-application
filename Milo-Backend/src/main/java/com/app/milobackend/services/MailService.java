package com.app.milobackend.services;

import com.app.milobackend.dtos.FilterDTO;
import com.app.milobackend.dtos.MailDTO;
import com.app.milobackend.filter.Criteria;
import com.app.milobackend.filter.CriteriaFactory;
import com.app.milobackend.models.Attachment;
import com.app.milobackend.models.Folder;
import com.app.milobackend.models.Mail;
import com.app.milobackend.repositories.AttachmentRepository;
//import com.app.milobackend.repositories.FolderRepo;
import com.app.milobackend.repositories.MailRepo;
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
//        LocalDateTime sentTime = LocalDateTime.now(ZoneId.of("Africa/Cairo"));
//        Folder folder = null;
//        if (mailDTO.getFolder() != null) {
//            folder = folderRepo.findByName(mailDTO.getFolder())
//                    .orElseThrow(() -> new RuntimeException("Folder not found"));
//        }
//        List<Attachment> attachments = new ArrayList<>();
//        if (mailDTO.getAttachments() != null) {
//            for (MultipartFile mf : mailDTO.getAttachments()) {
//                Attachment att = Attachment.builder()
//                        .fileName(mf.getOriginalFilename())
//                        .content(mf.getBytes())
//                        .build();
//                attachments.add(att);
//            }
//        }
        Mail mail=Mail.builder()
                .sender(mailDTO.getSenderEmail())
                .receiver(mailDTO.getReceiverEmail())
                .subject(mailDTO.getSubject())
                .body(mailDTO.getBody())
                .read(mailDTO.isRead())
                .active(mailDTO.isActive())
                .starred(mailDTO.isStarred())
                .hasAttachment(mailDTO.isHasAttachment())
                .priority(mailDTO.getPriority())
//                .folder(folder)
//                .attachments(attachments)
                .build();

        return  mail;



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
//            case "receiver":
//                sortworker.setStrategy(new SortByReceiver());
//                break;
            case "body":
                sortworker.setStrategy(new SortByBody());
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

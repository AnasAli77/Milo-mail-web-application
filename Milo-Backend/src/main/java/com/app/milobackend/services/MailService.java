package com.app.milobackend.services;

import com.app.milobackend.dtos.FilterDTO;
import com.app.milobackend.dtos.MailDTO;
import com.app.milobackend.filter.Criteria;
import com.app.milobackend.filter.CriteriaFactory;
import com.app.milobackend.mappers.MailMapperImpl;
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

    public void saveMail(MailDTO mailDTO) throws RuntimeException {

        Mail mail = mailMapper.toEntity(mailDTO);
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

    public void toggleStarredMail(Long mailId) {
        Mail mail = mailRepo.findById(mailId).orElse(null);
        if (mail != null) {
            mail.setStarred(!mail.isStarred());
        }
    }
}

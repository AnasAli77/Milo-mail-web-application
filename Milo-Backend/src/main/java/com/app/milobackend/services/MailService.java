package com.app.milobackend.services;

import com.app.milobackend.dtos.FilterDTO;
import com.app.milobackend.dtos.MailDTO;
import com.app.milobackend.filter.Criteria;
import com.app.milobackend.filter.CriteriaFactory;
import com.app.milobackend.mappers.MailMapperImpl;
import com.app.milobackend.models.Mail;
import com.app.milobackend.repositories.AttachmentRepository;
import com.app.milobackend.repositories.FolderRepo;
import com.app.milobackend.repositories.MailRepo;
import com.app.milobackend.repositories.UserRepo;
import com.app.milobackend.strategies.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

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

    // Restoring your cache
    private List<Mail> allMails;

    @Autowired
    public MailService(MailRepo mailRepo) {
        this.mailRepo = mailRepo;
        this.allMails = this.mailRepo.findAllWithDetails();
    }

    public void deleteMail(Long id) {
        if (mailRepo.findById(id).isPresent()) {
            mailRepo.deleteById(id);
            allMails.removeIf(mail -> mail.getId().equals(id));
        }
    }

    public List<Mail> GetAllMails() {
        return allMails;
    }

    public Mail GetMailById(long id) {
        return mailRepo.findById(id).orElse(null);
    }

    public void AddMail(Mail mail) {
        Mail savedMail = mailRepo.save(mail);
        allMails.add(savedMail);
    }

    public Mail UpdateMail(Mail mail) {
        allMails.removeIf(m -> m.getId().equals(mail.getId()));
        Mail updatedMail = mailRepo.save(mail);
        allMails.add(updatedMail);
        return updatedMail;
    }

    public void saveMail(MailDTO mailDTO) throws RuntimeException {
        Mail mail = mailMapper.toEntity(mailDTO);
        Mail savedMail = mailRepo.save(mail);
        allMails.add(savedMail);
    }

    public List<Mail> getMailsByFolder(String folderName, int pageNumber, int pageSize) {
        Pageable pageable = PageRequest.of(pageNumber, pageSize, Sort.by("sentAt").descending());
        Page<Mail> mailPage = mailRepo.findByFolder(folderName, pageable);
        return mailPage.getContent();
    }

    public List<Mail> getSortedMails(String sortBy) {
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
        return sortworker.sort(allMails);
    }

    public List<Mail> Filter(FilterDTO request){
        String word = request.getWord();
        List<String> selectedCriteria = request.getCriteria();
        if(selectedCriteria == null || selectedCriteria.isEmpty()){
            selectedCriteria = CriteriaFactory.allCriteriaNames();
        }
        List<Mail> filteredMails = new ArrayList<>();
        for(String name : selectedCriteria){
            Criteria criteria = CriteriaFactory.create(name, word);
            if(criteria != null){
                filteredMails.addAll(criteria.filter(allMails));
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

package com.app.milobackend.services;

import com.app.milobackend.models.Mail;
import com.app.milobackend.repositories.MailRepo;
import com.app.milobackend.strategies.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class MailService {
    @Autowired
    MailRepo mailRepo;


    public void delete(String id) {
        UUID uuid = UUID.fromString(id);
        mailRepo.deleteById(uuid);

    }
    public List<Mail> GetAllMails()
    {
        return mailRepo.findAll();
    }
    public Mail GetMailById(UUID id) {
        return mailRepo.findById(id).get();
    }
    public Mail AddMail(Mail mail) {
        return mailRepo.save(mail);
    }
    public Mail UpdateMail(Mail mail) {
        return mailRepo.save(mail);
    }
    public Mail DeleteMail(UUID id) {
        Mail mail = mailRepo.findById(id).get();
        mailRepo.delete(mail);
        return mail;
    }

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
            default:
                throw new IllegalArgumentException("Invalid sort by");

        }
        return sortworker.sort(mails);

    }

}

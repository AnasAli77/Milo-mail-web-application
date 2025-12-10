package com.app.milobackend.strategies;

import com.app.milobackend.models.Mail;
import com.app.milobackend.repositories.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;




public class SortByAttachment implements MailSortingStrategy {

    @Override
    public List<Mail> SortingMails(List<Mail> mails) {
        List<Mail> copy=new ArrayList<>(mails);
        map<Mail,int>
        for(Mail mail:copy){

        }

        return copy;
    }
}

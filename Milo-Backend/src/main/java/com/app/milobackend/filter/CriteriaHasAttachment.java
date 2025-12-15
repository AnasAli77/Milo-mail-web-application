package com.app.milobackend.filter;

import com.app.milobackend.models.Mail;

import java.util.ArrayList;
import java.util.List;

public class CriteriaHasAttachment implements Criteria{
    @Override
    public List<Mail> filter(List<Mail> mails) {
        List<Mail> filteredMails = new ArrayList<>();
        for (Mail mail : mails) {
            if(mail.isHasAttachment()){
                filteredMails.add(mail);
            }
        }
        return filteredMails;
    }
}

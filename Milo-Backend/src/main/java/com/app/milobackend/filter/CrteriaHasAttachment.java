package com.app.milobackend.filter;

import com.app.milobackend.models.Mail;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class CrteriaHasAttachment implements Criteria{
    @Override
    public List<Mail> filter(List<Mail> mails) {
        List<Mail> filterdMails = new ArrayList<>();
        for (Mail mail : mails) {
            if(mail.isHasAttachment()){
                filterdMails.add(mail);
            }
        }
        return filterdMails;
    }
}

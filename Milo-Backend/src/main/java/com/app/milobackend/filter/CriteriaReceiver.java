package com.app.milobackend.filter;

import com.app.milobackend.models.Mail;

import java.util.ArrayList;
import java.util.List;

public class CriteriaReceiver implements Criteria {
    private String word;
    public CriteriaReceiver(String word) {
        this.word = word.toLowerCase();
    }

    @Override
    public List<Mail> filter(List<Mail> mails) {
        List<Mail> mailsFiltered = new ArrayList<>();
        for (Mail mail : mails) {
            if(mail.getReceiver().toLowerCase().contains(this.word.toLowerCase())){
                mailsFiltered.add(mail);
            }
        }
        return mailsFiltered;
    }
}

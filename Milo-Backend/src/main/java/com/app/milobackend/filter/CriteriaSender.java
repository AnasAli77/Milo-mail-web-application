package com.app.milobackend.filter;

import com.app.milobackend.models.Mail;

import java.util.ArrayList;
import java.util.List;

public class CriteriaSender implements Criteria {
    private final String word;
    public CriteriaSender(String word) {
        this.word = word.toLowerCase();
    }

    @Override
    public List<Mail> filter(List<Mail> mails) {
        List<Mail> mailsFiltered = new ArrayList<>();
        for (Mail mail : mails) {
            if(mail.getSender().getName().toLowerCase().contains(this.word.toLowerCase())){
                mailsFiltered.add(mail);
            }
            if(mail.getSender().getEmail().toLowerCase().contains(this.word.toLowerCase())){
                mailsFiltered.add(mail);
            }
        }
        return mailsFiltered;
    }
}

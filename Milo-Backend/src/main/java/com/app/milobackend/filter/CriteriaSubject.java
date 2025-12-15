package com.app.milobackend.filter;

import com.app.milobackend.models.Mail;

import java.util.ArrayList;
import java.util.List;

public class CriteriaSubject implements Criteria {
    private final String word;
    public CriteriaSubject(String word) {
        this.word = word.toLowerCase();
    }
    @Override
    public List<Mail> filter(List<Mail> mails) {
        List<Mail> filteredMails = new ArrayList<>();
        for(Mail mail : mails){
            if(mail.getSubject().toLowerCase().contains(this.word)){
                filteredMails.add(mail);

            }
        }
        return filteredMails;
    }
}

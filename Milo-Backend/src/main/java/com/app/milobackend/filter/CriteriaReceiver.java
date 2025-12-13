package com.app.milobackend.filter;

import com.app.milobackend.models.ClientUser;
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
            ClientUser receiver = mail.getReceiver();
            if (receiver != null && receiver.getEmail().toLowerCase().contains(this.word)) {
                mailsFiltered.add(mail);
            }
            if(receiver != null && receiver.getName().toLowerCase().contains(this.word)){
                mailsFiltered.add(mail);
            }
        }
        return mailsFiltered;
    }
}

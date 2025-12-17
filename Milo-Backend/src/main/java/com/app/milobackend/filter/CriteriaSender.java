package com.app.milobackend.filter;

import com.app.milobackend.models.Mail;

import java.util.ArrayList;
import java.util.List;

public class CriteriaSender implements Criteria {
    private final String word;

    public CriteriaSender(String word) {
        this.word = word != null ? word.toLowerCase() : "";
    }

    @Override
    public List<Mail> filter(List<Mail> mails) {
        List<Mail> mailsFiltered = new ArrayList<>();
        for (Mail mail : mails) {
            if (mail.getSender() == null)
                continue;

            String senderName = mail.getSender().getName();
            String senderEmail = mail.getSender().getEmail();

            boolean matchesName = senderName != null &&
                    senderName.toLowerCase().contains(this.word);
            boolean matchesEmail = senderEmail != null &&
                    senderEmail.toLowerCase().contains(this.word);

            if (matchesName || matchesEmail) {
                mailsFiltered.add(mail);
            }
        }
        return mailsFiltered;
    }
}

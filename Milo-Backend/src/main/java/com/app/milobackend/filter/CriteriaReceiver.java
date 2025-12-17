package com.app.milobackend.filter;

import com.app.milobackend.models.ClientUser;
import com.app.milobackend.models.Mail;

import java.util.ArrayList;
import java.util.List;

public class CriteriaReceiver implements Criteria {
    private final String word;

    public CriteriaReceiver(String word) {
        this.word = word != null ? word.toLowerCase() : "";
    }

    @Override
    public List<Mail> filter(List<Mail> mails) {
        List<Mail> mailsFiltered = new ArrayList<>();
        for (Mail mail : mails) {
            ClientUser receiver = mail.getReceiver();
            if (receiver == null)
                continue;

            String receiverEmail = receiver.getEmail();
            String receiverName = receiver.getName();

            boolean matchesEmail = receiverEmail != null &&
                    receiverEmail.toLowerCase().contains(this.word);
            boolean matchesName = receiverName != null &&
                    receiverName.toLowerCase().contains(this.word);

            if (matchesEmail || matchesName) {
                mailsFiltered.add(mail);
            }
        }
        return mailsFiltered;
    }
}

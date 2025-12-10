package com.app.milobackend.strategies;

import com.app.milobackend.models.Mail;

import java.util.List;

public class SortByAttachment implements MailSortingStrategy {
    @Override
    public List<Mail> SortingMails(List<Mail> mails) {
        return List.of();
    }
}

package com.app.milobackend.strategies;

import com.app.milobackend.models.Mail;

import java.util.List;

public interface MailSortingStrategy {
    List<Mail> SortingMails(List<Mail> mails);
}

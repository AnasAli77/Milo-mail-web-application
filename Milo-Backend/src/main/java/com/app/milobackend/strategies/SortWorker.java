package com.app.milobackend.strategies;

import com.app.milobackend.models.Mail;

import java.util.List;

public class SortWorker {
    private MailSortingStrategy strategy;

    public void setStrategy(MailSortingStrategy strategy) {
        this.strategy = strategy;
    }

    public List<Mail> sort(List<Mail> mails) {
        return this.strategy.SortingMails(mails);
    }
}

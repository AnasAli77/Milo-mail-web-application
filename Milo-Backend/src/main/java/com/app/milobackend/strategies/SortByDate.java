package com.app.milobackend.strategies;

import com.app.milobackend.models.Mail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortByDate implements MailSortingStrategy {
    @Override
    public List<Mail> SortingMails(List<Mail> mails) {
        List<Mail> copy = new ArrayList<Mail>(mails);
        copy.sort(new Comparator<Mail>() {
            @Override
            public int compare(Mail o1, Mail o2) {
                return (o2.getSentAt().compareTo(o1.getSentAt()));
            }
        });
        return copy;
    }
}

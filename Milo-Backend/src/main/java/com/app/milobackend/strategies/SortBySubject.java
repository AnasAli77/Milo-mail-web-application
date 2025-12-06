package com.app.milobackend.strategies;

import com.app.milobackend.models.Mail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortBySubject implements MailSortingStrategy {
    @Override
    public List<Mail> SortingMails(List<Mail> mails) {
        List <Mail> copy=new ArrayList<>(mails);
        copy.sort(new Comparator<Mail>() {
            @Override
            public int compare(Mail m1, Mail m2) {
                return m1.getSubject().compareToIgnoreCase(m2.getSubject());
            }
        });
        return copy;
    }
}

package com.app.milobackend.strategies;

import com.app.milobackend.models.Mail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class SortByBody implements MailSortingStrategy {

    @Override
    public List<Mail> SortingMails(List<Mail> mails) {
        List<Mail> copy=new ArrayList<>(mails);
        copy.sort(new Comparator<Mail>() {
            @Override
            public int compare(Mail o1, Mail o2) {
                return o1.getBody().compareToIgnoreCase(o2.getBody());
            }
        });
        return copy;
    }
}

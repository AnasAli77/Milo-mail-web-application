package com.app.milobackend.filter;

import com.app.milobackend.models.Mail;

import java.util.ArrayList;
import java.util.List;

public class CriteriaYear implements Criteria {
    private final int year;
    public CriteriaYear(String year) {
        this.year = Integer.parseInt(year);
    }
    @Override
    public List<Mail> filter(List<Mail> mails) {
        List<Mail> mailsFiltered = new ArrayList<>();
        for(Mail mail:mails){
            if(mail.getSentAt().getYear()==this.year){
                mailsFiltered.add(mail);
            }
        }
        return mailsFiltered;
    }
}

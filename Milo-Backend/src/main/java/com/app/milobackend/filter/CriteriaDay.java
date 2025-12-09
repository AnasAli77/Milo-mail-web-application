package com.app.milobackend.filter;

import com.app.milobackend.models.Mail;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class CriteriaDay implements Criteria {
    private final int day;
    public CriteriaDay(String day) {
        this.day = Integer.parseInt(day);
    }
    @Override
    public List<Mail> filter(List<Mail> mails) {
        List<Mail> mailsFiltered = new ArrayList<>();
        for(Mail mail:mails){
            LocalDateTime dt = LocalDateTime.ofInstant(mail.getSentAt(), ZoneOffset.UTC);
            if(dt.getDayOfMonth()==this.day){
                mailsFiltered.add(mail);
            }
        }
        return mailsFiltered;
    }

}

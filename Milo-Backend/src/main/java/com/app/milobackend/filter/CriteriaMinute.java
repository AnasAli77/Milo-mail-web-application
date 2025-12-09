package com.app.milobackend.filter;

import com.app.milobackend.models.Mail;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class CriteriaMinute implements Criteria {
    private int minute;
    public CriteriaMinute(String minute) {
        this.minute = Integer.parseInt(minute);
    }
    @Override
    public List<Mail> filter(List<Mail> mails) {
        List<Mail> mailsFiltered = new ArrayList<>();
        for(Mail mail:mails){
            LocalDateTime dt = LocalDateTime.ofInstant(mail.getSentAt(), ZoneOffset.UTC);
            if(dt.getMinute()==this.minute){
                mailsFiltered.add(mail);
            }
        }
        return mailsFiltered;
    }

}

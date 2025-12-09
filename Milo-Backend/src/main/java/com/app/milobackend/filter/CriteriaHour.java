package com.app.milobackend.filter;

import com.app.milobackend.models.Mail;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;

public class CriteriaHour implements Criteria{
    private int hour;
    public CriteriaHour(String hour)
    {
        this.hour=Integer.parseInt(hour);
    }
    @Override
    public List<Mail> filter(List<Mail> mails) {
        List<Mail> mailsFiltered = new ArrayList<>();
        for(Mail mail:mails){
            LocalDateTime dt = LocalDateTime.ofInstant(mail.getSentAt(), ZoneOffset.UTC);
            if(dt.getHour()==this.hour){
                mailsFiltered.add(mail);
            }
        }
        return mailsFiltered;
    }

}

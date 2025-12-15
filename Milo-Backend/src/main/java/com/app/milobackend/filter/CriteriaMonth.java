package com.app.milobackend.filter;

import com.app.milobackend.models.Mail;

import java.util.ArrayList;
import java.util.List;

public class CriteriaMonth implements Criteria{
    private final int month;
    public CriteriaMonth(String month){
        this.month=Integer.parseInt(month);
    }
    @Override
    public List<Mail> filter(List<Mail> mails) {
        List<Mail> mailsFiltered = new ArrayList<>();
        for(Mail mail:mails){
            if(mail.getSentAt().getMonthValue()==this.month){
                mailsFiltered.add(mail);
            }
        }
        return mailsFiltered;
    }
    }


package com.app.milobackend.filter;

import com.app.milobackend.models.Mail;

import java.util.ArrayList;
import java.util.List;

public class CriteriaPriority implements Criteria {
    int priority;
    public CriteriaPriority(String  priority) {
        this.priority=Integer.parseInt(priority);
    }
    @Override
    public List<Mail> filter(List<Mail> mails) {
        List<Mail> mailsFiltered = new ArrayList<>();
        for(Mail mail:mails){
            if(mail.getPriority()==this.priority){
                mailsFiltered.add(mail);

            }
        }
        return mailsFiltered;
    }
}

package com.app.milobackend.commands;

import com.app.milobackend.models.Mail;
import org.springframework.stereotype.Component;

@Component
public class StarAction implements FilterRuleAction {

    @Override
    public void execute(Mail mail, String targetValue) {
        mail.setStarred(true);
    }
}

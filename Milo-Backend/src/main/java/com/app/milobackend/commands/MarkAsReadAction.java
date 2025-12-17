package com.app.milobackend.commands;

import com.app.milobackend.models.Mail;
import org.springframework.stereotype.Component;

@Component
public class MarkAsReadAction implements FilterRuleAction {

    @Override
    public void execute(Mail mail, String targetValue) {
        mail.setRead(true);
    }
}

package com.app.milobackend.commands;

import com.app.milobackend.models.Mail;

public interface FilterRuleAction {
    void execute(Mail mail, String targetValue);
}

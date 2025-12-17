package com.app.milobackend.commands;

import com.app.milobackend.models.Mail;

public interface FilterRuleAction {
    // Each action takes a Mail and does something to it
    void execute(Mail mail, String targetValue);
}

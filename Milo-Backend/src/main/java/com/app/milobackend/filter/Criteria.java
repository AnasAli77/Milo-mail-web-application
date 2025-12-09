package com.app.milobackend.filter;

import com.app.milobackend.models.Mail;

import java.util.List;

public interface Criteria {
    public List<Mail> filter(List<Mail> mails);
}

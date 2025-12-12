package com.app.milobackend.strategies;

import com.app.milobackend.models.Mail;

import java.util.Comparator;

public class MailPriorityComparator implements Comparator<Mail> {
    @Override
    public int compare(Mail m1, Mail m2) {
        return Integer.compare(m2.getPriority(), m1.getPriority());
    }
}

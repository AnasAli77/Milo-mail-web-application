package com.app.milobackend.strategies;

import com.app.milobackend.models.Mail;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public class SortByPriority implements MailSortingStrategy {
    @Override
    public List<Mail> SortingMails(List<Mail> mails) {
        PriorityQueue<Mail> pq=new PriorityQueue<>(new MailPriorityComparator());
        pq.addAll(mails);
        List<Mail> sortedMails=new ArrayList<>();
        while(!pq.isEmpty()){
            sortedMails.add(pq.poll());
        }
        return sortedMails;
    }
}

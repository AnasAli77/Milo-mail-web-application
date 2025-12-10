package com.app.milobackend.strategies;

import com.app.milobackend.models.Mail;
import com.app.milobackend.repositories.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;




public class SortByAttachment implements MailSortingStrategy {

    @Override
    public List<Mail> SortingMails(List<Mail> mails) {
        List<Mail> copy=new ArrayList<>(mails);
        copy.sort(new Comparator<Mail>() {
            @Override
            public int compare(Mail m1, Mail m2) {
                int size1=m1.getAttachments().size();
                int size2=m2.getAttachments().size();
                return Integer.compare(size2, size1);
            }
        });

        return copy;
    }
}

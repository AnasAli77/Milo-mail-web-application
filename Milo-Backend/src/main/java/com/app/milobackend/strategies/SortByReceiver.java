package com.app.milobackend.strategies;

import com.app.milobackend.models.ClientUser;
import com.app.milobackend.models.Mail;
import com.app.milobackend.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
@Component
public class SortByReceiver implements MailSortingStrategy {
    @Autowired
    UserRepo userRepo;

    @Override
    public List<Mail> SortingMails(List<Mail> mails) {
        List<Mail> copy = new ArrayList<>(mails);
        List<ClientUser> clientUsers = userRepo.findAll();
        clientUsers.sort(new Comparator<ClientUser>() {
            @Override
            public int compare(ClientUser c1, ClientUser c2) {
                return c1.getEmail().compareToIgnoreCase(c2.getEmail());
            }
        });
        for(ClientUser clientUser : clientUsers){
            copy.addAll(clientUser.getReceivedMails());
        }
        return copy;
    }
}

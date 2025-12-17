package com.app.milobackend.commands;

import com.app.milobackend.models.Folder;
import com.app.milobackend.models.Mail;
import com.app.milobackend.repositories.FolderRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class MoveToAction implements FilterRuleAction {

    @Autowired
    private FolderRepo folderRepo;

    @Override
    public void execute(Mail mail, String targetValue) {
        // Get receiver's email from the mail itself
        String receiverEmail = mail.getReceiver().getEmail();

        Folder targetFolder = folderRepo.findByNameAndUserEmail(targetValue, receiverEmail);

        if (targetFolder != null) {
            targetFolder.addMail(mail);
            mail.setFolder(targetFolder);
        } else {
            // Fallback to inbox if target folder not found
            Folder receiverInbox = folderRepo.findByNameAndUserEmail("inbox", receiverEmail);
            if (receiverInbox != null) {
                receiverInbox.addMail(mail);
                mail.setFolder(receiverInbox);
            }
        }
    }
}

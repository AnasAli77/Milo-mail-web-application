package com.app.milobackend.services;

import com.app.milobackend.repositories.MailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class TrashCleanupService {

    @Autowired
    private MailRepo mailRepo;

    // Read retention time from application.properties (in minutes)
    @Value("${app.trash.retention-minutes}")
    private int retentionMinutes;

    // Run this task every 60000ms (1 minute)
    // You can change 'fixedRate' if you want it to run less often
    @Scheduled(fixedRate = 60000)
    @CacheEvict(value = "mails", allEntries = true) // Clear cache so deleted mails disappear from UI
    public void cleanupTrash() {
        // Calculate the cutoff date (Now - Retention Period)
        LocalDateTime cutoffDate = LocalDateTime.now().minusMinutes(retentionMinutes);

        System.out.println("----- Running Trash Cleanup -----");
        System.out.println("Deleting Trash mails older than: " + cutoffDate);

        mailRepo.deleteExpiredTrashMails(cutoffDate);
    }
}
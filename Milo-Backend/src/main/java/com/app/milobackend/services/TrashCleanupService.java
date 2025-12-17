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

    // Run this task every 10000ms (10 seconds)
    @Scheduled(fixedRate = 10000)
    @CacheEvict(value = "mails", allEntries = true) // Clear cache so deleted mails disappear from UI
    public void cleanupTrash() {
        // Calculate the cutoff date (Now - Retention Period)
        // Use same timezone as when trashedAt is set in MailService
        LocalDateTime cutoffDate = LocalDateTime.now(java.time.ZoneId.of("Africa/Cairo")).minusMinutes(retentionMinutes);

        System.out.println("----- Running Trash Cleanup -----");
        System.out.println("Retention minutes: " + retentionMinutes);
        System.out.println("Cutoff date (delete mails trashed before): " + cutoffDate);

        int deleted = mailRepo.deleteExpiredTrashMails(cutoffDate);
        System.out.println("Deleted " + deleted + " expired trash mails");
    }
}
package com.app.milobackend.services;

import com.app.milobackend.dtos.StatsDTO;
import com.app.milobackend.repositories.MailRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class StatsService {

    @Autowired
    private MailRepo mailRepo;

    public String getCurrentUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    @Transactional(readOnly = true)
    public StatsDTO getStats() {
        String userEmail = getCurrentUserEmail();
        if (userEmail == null) {
            throw new RuntimeException("User not authenticated");
        }

        // Calculate date for "this week" (last 7 days)
        LocalDateTime oneWeekAgo = LocalDateTime.now(ZoneId.of("Africa/Cairo")).minusDays(7);

        // Get counts
        int sentThisWeek = mailRepo.countSentSince(userEmail, oneWeekAgo);
        int received = mailRepo.countReceived(userEmail);
        int unread = mailRepo.countUnread(userEmail);

        // Get top contact
        List<Object[]> topContactResult = mailRepo.findTopContact(userEmail);
        String topContact = "";
        int topContactCount = 0;
        if (topContactResult != null && !topContactResult.isEmpty()) {
            Object[] row = topContactResult.get(0);
            topContact = (String) row[0];
            topContactCount = ((Number) row[1]).intValue();
        }

        // Get priority breakdown
        List<Object[]> priorityResults = mailRepo.countByPriority(userEmail);
        Map<Integer, Integer> priorityBreakdown = new HashMap<>();
        // Initialize all priorities to 0
        for (int i = 1; i <= 5; i++) {
            priorityBreakdown.put(i, 0);
        }
        if (priorityResults != null) {
            for (Object[] row : priorityResults) {
                int priority = ((Number) row[0]).intValue();
                int count = ((Number) row[1]).intValue();
                priorityBreakdown.put(priority, count);
            }
        }

        return StatsDTO.builder()
                .sentThisWeek(sentThisWeek)
                .received(received)
                .unread(unread)
                .topContact(topContact)
                .topContactCount(topContactCount)
                .priorityBreakdown(priorityBreakdown)
                .build();
    }
}

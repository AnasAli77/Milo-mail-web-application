package com.app.milobackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StatsDTO {
    private int sentThisWeek;
    private int received;
    private int unread;
    private String topContact;
    private int topContactCount;
    private Map<Integer, Integer> priorityBreakdown;
}

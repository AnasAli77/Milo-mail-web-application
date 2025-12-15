package com.app.milobackend.filter;

import java.util.Arrays;
import java.util.List;

public class CriteriaFactory {
    public static Criteria create(String name, String word) {
        if (name == null) return null;

        return switch (name.toLowerCase()) {
            case "body" -> new CriteriaBody(word);
            case "subject" -> new CriteriaSubject(word);
            case "sender" -> new CriteriaSender(word);
            case "receiver" -> new CriteriaReceiver(word);
            case "priority" -> new CriteriaPriority(word);
            case "day" -> new CriteriaDay(word);
            case "month" -> new CriteriaMonth(word);
            case "year" -> new CriteriaYear(word);
            case "hour" -> new CriteriaHour(word);
            case "minute" -> new CriteriaMinute(word);
            case "hasattachment" -> new CriteriaHasAttachment();
            default -> null;
        };
    }

    public static List<String> allCriteriaNames() {
        return Arrays.asList(
                "body", "subject", "sender", "receiver", "priority",
                "day", "month", "year", "hour", "minute"
        );
    }
}

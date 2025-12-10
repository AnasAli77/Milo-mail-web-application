package com.app.milobackend.filter;

import java.util.Arrays;
import java.util.List;

public class CriteriaFactory {
    public static Criteria create(String name, String word) {
        if (name == null) return null;

        switch (name.toLowerCase()) {
            case "body":
                return new CriteriaBody(word);

            case "subject":
                return new CriteriaSubject(word);

            case "sender":
                return new CriteriaSender(word);

           case "receiver":
                return new CriteriaReceiver(word);

            case "priority":
                return new CriteriaPriority(word);

            case "day":
                return new CriteriaDay(word);

            case "month":
                return new CriteriaMonth(word);

            case "year":
                return new CriteriaYear(word);

            case "hour":
                return new CriteriaHour(word);

            case "minute":
                return new CriteriaMinute(word);
            case "hasattachment":
                return new CrteriaHasAttachment();

            default:
                return null;
        }
    }

    public static List<String> allCriteriaNames() {
        return Arrays.asList(
                "body", "subject", "sender", "receiver", "priority",
                "day", "month", "year", "hour", "minute"
        );
    }
}

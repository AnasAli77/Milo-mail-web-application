package com.app.milobackend.commands;

import org.springframework.stereotype.Component;
import java.util.Map;

@Component
public class ActionFactory {

    private final Map<String, FilterRuleAction> actionMap;

    public ActionFactory(
            MoveToAction moveToAction,
            StarAction starAction,
            MarkAsReadAction markAsReadAction) {
        this.actionMap = Map.of(
                "move", moveToAction,
                "star", starAction,
                "markasread", markAsReadAction);
    }

    public FilterRuleAction getAction(String actionType) {
        if (actionType == null)
            return null;
        return actionMap.get(actionType.toLowerCase());
    }
}

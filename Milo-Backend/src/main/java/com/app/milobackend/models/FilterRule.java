package com.app.milobackend.models;

import com.app.milobackend.commands.ActionFactory;
import com.app.milobackend.commands.FilterRuleAction;
import com.app.milobackend.dtos.FilterRuleDTO;
import com.app.milobackend.filter.Criteria;
import com.app.milobackend.filter.CriteriaFactory;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class FilterRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ElementCollection
    private Map<String, String> criteriaTypesValues = new HashMap<>();

    private String actionType; // e.g., "MOVE", "STAR", "READ"
    private String actionTarget; // e.g., Folder ID (for move), or null

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private ClientUser user; // Link to the user who created the rule

    public FilterRule(FilterRuleDTO dto, ClientUser user) {
        this.id = null;
        this.criteriaTypesValues = dto.getCriteriaTypesValues();
        this.actionType = dto.getActionType();
        this.actionTarget = dto.getActionTarget();
        this.user = user;
    }

    public boolean check(Mail mail) {
        // Map<String, String> criteriaMap = request.getKeys();
        List<Mail> mailList = new ArrayList<>(List.of(mail));

        for (Map.Entry<String, String> entry : criteriaTypesValues.entrySet()) {

            String criteriaType = entry.getKey();
            String criteriaValue = entry.getValue();

            if (criteriaValue == null || criteriaValue.isBlank()) {
                continue;
            }

            Criteria criteria = CriteriaFactory.create(criteriaType, criteriaValue);

            if (criteria != null) {
                mailList = criteria.filter(mailList);
            }

            if (mailList.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    public void apply(Mail mail, ActionFactory actionFactory) {
        FilterRuleAction action = actionFactory.getAction(this.actionType);
        if (action != null) {
            System.err.println("Action: " + this.actionType);
            action.execute(mail, this.actionTarget);
        }
    }
}

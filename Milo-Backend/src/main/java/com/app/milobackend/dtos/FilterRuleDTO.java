package com.app.milobackend.dtos;

import com.app.milobackend.models.FilterRule;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.HashMap;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilterRuleDTO {
    private Long id;
    Map<String, String> criteriaTypesValues = new HashMap<>();
    private String actionType;
    private String actionTarget;

    public FilterRuleDTO(FilterRule filterRule) {
        this.id = filterRule.getId();
        this.criteriaTypesValues = filterRule.getCriteriaTypesValues();
        this.actionType = filterRule.getActionType();
        this.actionTarget = filterRule.getActionTarget();
    }
}

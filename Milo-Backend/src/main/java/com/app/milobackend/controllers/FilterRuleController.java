package com.app.milobackend.controllers;

import com.app.milobackend.dtos.FilterRuleDTO;
import com.app.milobackend.services.FilterRuleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/filter")
public class FilterRuleController {

    private final FilterRuleService filterRuleService;

    public FilterRuleController(FilterRuleService filterRuleService) {
        this.filterRuleService = filterRuleService;
    }

    @GetMapping("/all")
    public List<FilterRuleDTO> getFilterRules() {
        return filterRuleService.getFilters();
    }

    @DeleteMapping("/delete/{filterRuleId}")
    public void deleteFilterRule(@PathVariable("filterRuleId") Long id) {
        filterRuleService.deleteFilter(id);
    }

    @PostMapping("/add")
    public void addFilterRule(@RequestBody FilterRuleDTO dto) {
        filterRuleService.addFilter(dto);
    }
}

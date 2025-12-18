package com.app.milobackend.services;

import com.app.milobackend.dtos.FilterRuleDTO;
import com.app.milobackend.models.ClientUser;
import com.app.milobackend.models.FilterRule;
import com.app.milobackend.repositories.FilterRuleRepo;
import com.app.milobackend.repositories.UserRepo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FilterRuleService {

    private final FilterRuleRepo filterRuleRepo;

    private final UserRepo userRepo;

    public FilterRuleService(FilterRuleRepo filterRuleRepo, UserRepo userRepo) {
        this.filterRuleRepo = filterRuleRepo;
        this.userRepo = userRepo;
    }

    public String getCurrentUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    @Transactional(readOnly = true)
    @Cacheable(value = "filterRules", key = "#root.target.getCurrentUserEmail()")
    public List<FilterRuleDTO> getFilters() {
        String userEmail = getCurrentUserEmail();
        List<FilterRule> filterRules = filterRuleRepo.findByUserEmail(userEmail);

        return filterRules.stream().map((FilterRuleDTO::new)).toList();
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "filterRules", allEntries = true),
            @CacheEvict(value = "filterRuleEntities", allEntries = true)
    })
    public void deleteFilter(Long filterId) {
        String userEmail = getCurrentUserEmail();

        // Verify ownership before deletion
        var filterRule = filterRuleRepo.findByIdAndUserEmail(filterId, userEmail);
        if (filterRule.isEmpty()) {
            throw new RuntimeException("Filter not found or access denied");
        }

        // Now delete it
        filterRuleRepo.deleteFilterRuleById(filterId);
    }

    @Transactional
    @Caching(evict = {
            @CacheEvict(value = "filterRules", allEntries = true),
            @CacheEvict(value = "filterRuleEntities", allEntries = true)
    })
    public void addFilter(FilterRuleDTO dto) {
        String userEmail = getCurrentUserEmail();
        ClientUser user = userRepo.findByEmail(userEmail);

        FilterRule filterRule = new FilterRule(dto, user);

        filterRuleRepo.save(filterRule);
    }
}

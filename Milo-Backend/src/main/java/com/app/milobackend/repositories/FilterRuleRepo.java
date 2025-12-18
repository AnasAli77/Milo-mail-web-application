package com.app.milobackend.repositories;

import com.app.milobackend.models.Contact;
import com.app.milobackend.models.FilterRule;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface FilterRuleRepo extends JpaRepository<FilterRule, Long> {

    @Transactional(readOnly = true)
    @Cacheable(value = "filterRuleEntities", key = "#email")
    List<FilterRule> findByUserEmail(String email);

    @Modifying
    @Query("DELETE FROM FilterRule f WHERE f.id = :filterId")
    @Caching(evict = {
            @CacheEvict(value = "filterRules", allEntries = true),
            @CacheEvict(value = "filterRuleEntities", allEntries = true)
    })
    void deleteFilterRuleById(@Param("filterId") Long id);

    @Transactional(readOnly = true)
    Optional<FilterRule> findByIdAndUserEmail(Long id, String email);
}

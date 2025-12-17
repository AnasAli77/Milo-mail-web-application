package com.app.milobackend.repositories;

import com.app.milobackend.models.Contact;
import com.app.milobackend.models.FilterRule;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface FilterRuleRepo extends JpaRepository<FilterRule, Long> {

    @Transactional(readOnly = true)
    @Cacheable(value = "filterRuleEntities", key = "#email")
    List<FilterRule> findByUserEmail(String email);

    @Modifying
    @Transactional
    @Query("DELETE FROM FilterRule f WHERE f.id = :filterId AND f.user.email = :userEmail")
    void deleteFilterRuleByIdANDUser(@Param("filterId") Long id, @Param("userEmail") String email);
}

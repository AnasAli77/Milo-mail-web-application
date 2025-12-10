package com.app.milobackend.repositories;

import com.app.milobackend.models.Mail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MailRepo extends JpaRepository<Mail, Long> {
    List<Mail> findByStarredTrue();

    @Query("SELECT m FROM Mail m WHERE m.folder.name = :folderName")
    Page<Mail> findByFolder(@Param("folderName") String folderName, Pageable pageable);
}

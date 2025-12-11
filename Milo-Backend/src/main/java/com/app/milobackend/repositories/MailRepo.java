package com.app.milobackend.repositories;

import com.app.milobackend.models.Mail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface MailRepo extends JpaRepository<Mail, Long> {
    Page<Mail> findByStarredTrue(Pageable pageable);

    @Query("SELECT m FROM Mail m WHERE m.folder.name = :folderName")
    Page<Mail> findByFolder(@Param("folderName") String folderName, Pageable pageable);

//    @Query("SELECT m FROM Mail m " +
//            "LEFT JOIN FETCH m.attachments " +
//            "LEFT JOIN FETCH m.sender " +
//            "LEFT JOIN FETCH m.receivers")
//    List<Mail> findAllWithDetails();

    @EntityGraph(attributePaths = {"folder","sender","receivers","attachments"})
    @Query("select distinct m from Mail m")
    List<Mail> findAllWithDetails();

}

package com.app.milobackend.repositories;

import com.app.milobackend.models.Mail;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface MailRepo extends JpaRepository<Mail, Long> {
    @EntityGraph(attributePaths = {"folder","sender","receiver","attachments"})
    @Query("select distinct m from Mail m")
    List<Mail> findAllWithDetails();

    @EntityGraph(attributePaths = {"folder", "sender", "receiver", "attachments"})
    List<Mail> findByIdIn(List<Long> ids);

    // Check if User is (Sender OR Receiver) AND Mail is Starred
    @Query("SELECT DISTINCT m FROM Mail m WHERE m.starred = true AND (m.sender.email = :email OR m.receiver.email = :email)")
    Page<Mail> findStarredMailsForUser(@Param("email") String email, Pageable pageable);

    // Check if User is a Receiver AND Folder is correct for inbox
    @Query("SELECT DISTINCT m FROM Mail m WHERE m.folder.name = :folderName AND m.receiver.email = :email")
    Page<Mail> findReceivedMailsByFolder(@Param("folderName") String folderName, @Param("email") String email, Pageable pageable);

    // Check if User is the Sender AND Folder is correct for sent/drafts
    @Query("SELECT m FROM Mail m WHERE m.folder.name = :folderName AND m.sender.email = :email")
    Page<Mail> findSentMailsByFolder(@Param("folderName") String folderName, @Param("email") String email, Pageable pageable);

    // Check if User is (Sender OR Receiver) AND Folder is correct for Other/Generic
    @Query("SELECT DISTINCT m FROM Mail m WHERE m.folder.name = :folderName AND (m.sender.email = :email OR m.receiver.email = :email)")
    Page<Mail> findMailsByFolderAndUserInvolvement(@Param("folderName") String folderName, @Param("email") String email, Pageable pageable);

    @Query("SELECT DISTINCT m FROM Mail m WHERE (m.sender.email = :email OR m.receiver.email = :email)")
    Page<Mail> findMailsByUserInvolvement(@Param("email") String email, Pageable pageable);

    @Modifying
    @Transactional
    @Query("DELETE FROM Mail m WHERE m.folder.name = 'Trash' AND m.trashedAt < :cutoffDate")
    void deleteExpiredTrashMails(@Param("cutoffDate") LocalDateTime cutoffDate);
}

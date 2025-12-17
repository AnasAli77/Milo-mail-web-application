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
        @EntityGraph(attributePaths = { "folder", "sender", "receiver", "attachments" })
        @Query("select distinct m from Mail m")
        List<Mail> findAllWithDetails();

        @EntityGraph(attributePaths = { "folder", "sender", "receiver", "attachments" })
        List<Mail> findByIdIn(List<Long> ids);

        // Check if User is (Sender OR Receiver) AND Mail is Starred
        // Returns deduplicated results - only one copy per unique mail (prefers inbox
        // over sent)
        @EntityGraph(attributePaths = { "folder", "sender", "receiver", "attachments" })
        @Query("SELECT m FROM Mail m WHERE m.starred = true AND (m.sender.email = :email OR m.receiver.email = :email) "
                        +
                        "AND m.id = (SELECT MIN(m2.id) FROM Mail m2 WHERE m2.starred = true " +
                        "AND m2.subject = m.subject AND m2.body = m.body AND m2.sentAt = m.sentAt AND m2.sender = m.sender "
                        +
                        "AND (m2.sender.email = :email OR m2.receiver.email = :email))")
        Page<Mail> findStarredMailsForUser(@Param("email") String email, Pageable pageable);

        // Find all copies of the same email for a user (for syncing star status)
        // Matches by subject, body, sentAt, and sender to identify related copies
        @Query("SELECT m FROM Mail m WHERE m.subject = :subject AND m.body = :body " +
                        "AND m.sentAt = :sentAt AND m.sender.email = :senderEmail " +
                        "AND (m.sender.email = :userEmail OR m.receiver.email = :userEmail)")
        List<Mail> findRelatedCopiesForUser(
                        @Param("subject") String subject,
                        @Param("body") String body,
                        @Param("sentAt") LocalDateTime sentAt,
                        @Param("senderEmail") String senderEmail,
                        @Param("userEmail") String userEmail);

        // Check if User is a Receiver AND Folder is correct for inbox
        @EntityGraph(attributePaths = { "folder", "sender", "receiver", "attachments" })
        @Query("SELECT DISTINCT m FROM Mail m WHERE m.folder.name = :folderName AND m.receiver.email = :email")
        Page<Mail> findReceivedMailsByFolder(@Param("folderName") String folderName, @Param("email") String email,
                        Pageable pageable);

        // Check if User is the Sender AND Folder is correct for sent/drafts
        @EntityGraph(attributePaths = { "folder", "sender", "receiver", "attachments" })
        @Query("SELECT m FROM Mail m WHERE m.folder.name = :folderName AND m.sender.email = :email")
        Page<Mail> findSentMailsByFolder(@Param("folderName") String folderName, @Param("email") String email,
                        Pageable pageable);

        // Check if User is (Sender OR Receiver) AND Folder is correct for Other/Generic
        @EntityGraph(attributePaths = { "folder", "sender", "receiver", "attachments" })
        @Query("SELECT DISTINCT m FROM Mail m WHERE m.folder.name = :folderName AND (m.sender.email = :email OR m.receiver.email = :email)")
        Page<Mail> findMailsByFolderAndUserInvolvement(@Param("folderName") String folderName,
                        @Param("email") String email,
                        Pageable pageable);

        @EntityGraph(attributePaths = { "folder", "sender", "receiver", "attachments" })
        @Query("SELECT DISTINCT m FROM Mail m WHERE (m.sender.email = :email OR m.receiver.email = :email)")
        Page<Mail> findMailsByUserInvolvement(@Param("email") String email, Pageable pageable);

        @Modifying
        @Transactional
        @Query("DELETE FROM Mail m WHERE LOWER(m.folder.name) = 'trash' AND m.trashedAt < :cutoffDate")
        int deleteExpiredTrashMails(@Param("cutoffDate") LocalDateTime cutoffDate);

        // Count emails sent by user since a date (for "sent this week")
        // Only count sender's copy (receiver IS NULL) to avoid counting duplicates
        @Query("SELECT COUNT(m) FROM Mail m WHERE m.sender.email = :email AND m.receiver IS NULL AND m.folder.name != 'drafts' AND m.sentAt > :since")
        int countSentSince(@Param("email") String email, @Param("since") LocalDateTime since);

        // Count total received emails
        @Query("SELECT COUNT(m) FROM Mail m WHERE m.receiver.email = :email")
        int countReceived(@Param("email") String email);

        // Count unread emails in inbox
        @Query("SELECT COUNT(m) FROM Mail m WHERE m.receiver.email = :email AND m.read = false AND m.folder.name = 'inbox'")
        int countUnread(@Param("email") String email);

        // Find top contact (who user emails most)
        @Query("SELECT m.receiver.email, COUNT(m) as cnt FROM Mail m WHERE m.sender.email = :email AND m.receiver.email IS NOT NULL GROUP BY m.receiver.email ORDER BY cnt DESC")
        List<Object[]> findTopContact(@Param("email") String email);

        // Count emails by priority for the user (both sent and received)
        @Query("SELECT m.priority, COUNT(m) FROM Mail m WHERE ((m.sender.email = :email AND m.receiver IS NULL) OR m.receiver.email = :email) GROUP BY m.priority")
        List<Object[]> countByPriority(@Param("email") String email);
}

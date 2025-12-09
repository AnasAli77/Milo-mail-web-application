package com.app.milobackend.repositories;

import com.app.milobackend.models.Mail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MailRepo extends JpaRepository<Mail, Long> {
}

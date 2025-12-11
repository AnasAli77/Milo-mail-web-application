package com.app.milobackend.repositories;

import com.app.milobackend.models.AttachmentContent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AttachmentContentRepo extends JpaRepository<AttachmentContent, Long> {
}
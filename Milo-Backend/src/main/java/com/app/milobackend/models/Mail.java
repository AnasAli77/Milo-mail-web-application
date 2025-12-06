package com.app.milobackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private User sender;
    private ArrayList<User> receiver;
    private String subject;
    private String body;
    private Instant sentAt =  Instant.now();
    private String priority;
    private ArrayList<Attachment> attachments;
    private String folderId;

}

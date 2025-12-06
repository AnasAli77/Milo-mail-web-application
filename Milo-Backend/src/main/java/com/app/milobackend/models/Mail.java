package com.app.milobackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private String sender;
    private String receiver;
    private String subject;
    private String body;
    private Instant sentAt =  Instant.now();
    private int priority; //from 1to 4 (map it in frontEnd)
    private ArrayList<Attachment> attachments;
    private String folderId;

}

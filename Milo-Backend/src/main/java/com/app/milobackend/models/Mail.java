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
    private Long id;

    private String sender;
    private String receiver;
    private String subject;

    @Column(columnDefinition = "TEXT")
    private String body;
    private Instant sentAt =  Instant.now();
    private int priority; //from 1to 4 (map it in frontEnd)

    @OneToMany(cascade = CascadeType.ALL)
    private List<Attachment> attachments = new ArrayList<>();
//    private String folderId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folderId")
    @JsonBackReference
    private Folder folder;

}

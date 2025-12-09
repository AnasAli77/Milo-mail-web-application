package com.app.milobackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
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

    // Many Mails can belong to one Folder.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    @JsonBackReference // When serializing Mail, the full Folder object will be omitted (breaking the loop with Folder's @JsonManagedReference)
    private Folder folder;

    // One Mail has many Attachments.
    @OneToMany(mappedBy = "mail", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Attachments for this mail WILL be serialized
    private List<Attachment> attachments = new ArrayList<>();

    public void addAttachment(Attachment m) {
        if (m == null) return;
        attachments.add(m);

    }

    public void removeMail(Attachment m) {
        if (m == null) return;
        attachments.remove(m);

    }
}

package com.app.milobackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Mail {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String sender;
    private List<String> receiver;
    private String subject;
    private boolean read;
    private boolean active;
    private boolean starred;
    private boolean hasAttachment;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Builder.Default
    private LocalDateTime sentAt =  LocalDateTime.now(ZoneId.of("Africa/Cairo"));
    private int priority; //from 1to 4 (map it in frontEnd)

    // Many Mails can belong to one Folder.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    @JsonBackReference // When serializing Mail, the full Folder object will be omitted (breaking the loop with Folder's @JsonManagedReference)
    private Folder folder;

    // One Mail has many Attachments.
    @OneToMany(mappedBy = "mail", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Attachments for this mail WILL be serialized
    @Builder.Default
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

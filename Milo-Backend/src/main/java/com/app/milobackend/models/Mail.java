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
    private String subject;
    private boolean read;
    private boolean active;
    private boolean starred;
    private boolean hasAttachment;

    // 1. SENDER: Linked via the 'email' column
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "sender_email", referencedColumnName = "email")
    private ClientUser sender;

    // 2. RECEIVERS: List of users, linked via their 'email'
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "mail_receivers", // Name of the hidden join table
            joinColumns = @JoinColumn(name = "mail_id"), // Key from Mail side
            inverseJoinColumns = @JoinColumn(name = "receiver_email", referencedColumnName = "email") // Key from User side
    )
    private List<ClientUser> receivers = new ArrayList<>();

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

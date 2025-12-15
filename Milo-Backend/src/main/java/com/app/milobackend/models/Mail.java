package com.app.milobackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ToString
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
public class Mail {
//    @Transient
    private LocalDateTime trashedAt;

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
    @ToString.Exclude
    private ClientUser sender;

//     2. RECEIVERS: List of users, linked via their 'email'
//    @ManyToMany(fetch = FetchType.LAZY)
//    @JoinTable(
//            name = "mail_receivers", // Name of the hidden join table
//            joinColumns = @JoinColumn(name = "mail_id"), // Key from Mail side
//            inverseJoinColumns = @JoinColumn(name = "receiver_email", referencedColumnName = "email") // Key from User side
//    )
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "email")
    @ToString.Exclude
    private ClientUser receiver;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Builder.Default
    private LocalDateTime sentAt =  LocalDateTime.now(ZoneId.of("Africa/Cairo"));
    private int priority; //from 1to 4 (map it in frontEnd)

    // Many Mails can belong to one Folder.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    @JsonBackReference // When serializing Mail, the full Folder object will be omitted (breaking the loop with Folder's @JsonManagedReference)
    @ToString.Exclude
    private Folder folder;

    // One Mail has many Attachments.
    @OneToMany(mappedBy = "mail", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference // Attachments for this mail WILL be serialized
    @Builder.Default
    @ToString.Exclude
    private Set<Attachment> attachments = new HashSet<>();

    // Copy Constructor
    public Mail(Mail source) {
        // never copy the ID nor the receiver

        this.id = null; // Reset ID
        this.subject = source.getSubject();
        this.body = source.getBody();
        this.priority = source.getPriority();
        this.sender = source.getSender(); // Shallow copy is fine for User
        this.sentAt = LocalDateTime.now();
        this.read = source.isRead();
        this.starred = false;
        this.hasAttachment = source.isHasAttachment();

        // Deep Copy Attachments
        this.attachments = new HashSet<>();
        if (source.getAttachments() != null) {
            for (Attachment att : source.getAttachments()) {
                Attachment newAtt = new Attachment();
                newAtt.setName(att.getName());
                newAtt.setType(att.getType());

                // IMPORTANT: Point to the same heavy content ID or Path
                // If using the Split Table approach:
                if(att.getContent() != null) {
                    // Create new content wrapper pointing to same bytes?
                    // Or simpler: Just duplicate the bytes for now (easiest logic)
                    // Ideally, you'd share the blob, but let's deep copy for safety first.
                    newAtt.setContent(new AttachmentContent(att.getContent().getData()));
                    newAtt.getContent().setAttachment(newAtt);
                }
                this.addAttachment(newAtt);
                newAtt.setMail(this);
            }
        }
    }

    // Copy Constructor with specific receiver (for Queue-based multi-recipient sending)
    public Mail(Mail source, ClientUser receiver) {
        this(source); // Call the base copy constructor
        this.receiver = receiver;
    }

    public void update(Mail source) {
        this.subject = source.getSubject();
        this.body = source.getBody();
        this.priority = source.getPriority();
        this.sender = source.getSender(); // Shallow copy is fine for User
        this.sentAt = LocalDateTime.now();
        this.read = source.isRead();
        this.starred = source.isStarred();
        this.hasAttachment = source.isHasAttachment();

        // Deep Copy Attachments
//        this.attachments = new HashSet<>();
        this.attachments.clear();
        if (source.getAttachments() != null && !source.getAttachments().isEmpty()) {
            List<Attachment> newAttachments = new ArrayList<>(source.getAttachments());
            for (Attachment att : newAttachments) {
//
                this.addAttachment(att);
                att.setMail(this);
            }
        }
    }

    public void addAttachment(Attachment a) {
        if (a == null) return;
        a.setMail(this);
        attachments.add(a);
    }

    public void removeAttachment(Attachment a) {
        if (a == null) return;
        attachments.remove(a);
        if (a.getMail() == this) a.setMail(null);
    }
}

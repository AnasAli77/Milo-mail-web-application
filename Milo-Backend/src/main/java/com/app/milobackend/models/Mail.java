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
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@Entity
@Table(name = "mails")
public class Mail implements Prototype<Mail> {
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receiver_id", referencedColumnName = "email")
    @ToString.Exclude
    private ClientUser receiver;

    @Column(columnDefinition = "TEXT")
    private String body;

    @Builder.Default
    private LocalDateTime sentAt = LocalDateTime.now(ZoneId.of("Africa/Cairo"));
    private int priority;

    // Many Mails can belong to one Folder.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "folder_id")
    @JsonBackReference
    @ToString.Exclude
    private Folder folder;

    // One Mail has many Attachments.
    @OneToMany(mappedBy = "mail", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    @Builder.Default
    @ToString.Exclude
    private Set<Attachment> attachments = new HashSet<>();

    @Override
    public Mail clone() {
        Mail clonedMail = new Mail();
        clonedMail.setId(null); // Reset ID for new entity
        clonedMail.setSubject(this.subject);
        clonedMail.setBody(this.body);
        clonedMail.setPriority(this.priority);
        clonedMail.setSender(this.sender); // Shallow copy is fine
        clonedMail.setSentAt(this.sentAt);
        clonedMail.setRead(this.read);
        clonedMail.setStarred(false); // Reset starred for new copy
        clonedMail.setHasAttachment(this.hasAttachment);
        clonedMail.setActive(this.active);

        // Deep Copy Attachments
        clonedMail.setAttachments(new HashSet<>());
        if (this.attachments != null) {
            for (Attachment att : this.attachments) {
                Attachment newAtt = new Attachment();
                newAtt.setName(att.getName());
                newAtt.setType(att.getType());
                newAtt.setSize(att.getSize());

                if (att.getContent() != null) {
                    newAtt.setContent(new AttachmentContent(att.getContent().getData()));
                    newAtt.getContent().setAttachment(newAtt);
                }
                clonedMail.addAttachment(newAtt);
                newAtt.setMail(clonedMail);
            }
        }

        return clonedMail;
    }

    public Mail cloneWithReceiver(ClientUser receiver) {
        Mail clonedMail = this.clone();
        clonedMail.setReceiver(receiver);
        return clonedMail;
    }

    public void update(Mail source) {
        this.subject = source.getSubject();
        this.body = source.getBody();
        this.priority = source.getPriority();
        this.sender = source.getSender();
        this.sentAt = LocalDateTime.now();
        this.read = source.isRead();
        this.starred = source.isStarred();
        this.hasAttachment = source.isHasAttachment();

        this.attachments.clear();
        if (source.getAttachments() != null && !source.getAttachments().isEmpty()) {
            List<Attachment> newAttachments = new ArrayList<>(source.getAttachments());
            for (Attachment att : newAttachments) {
                this.addAttachment(att);
                att.setMail(this);
            }
        }
    }

    public void addAttachment(Attachment a) {
        if (a == null)
            return;
        a.setMail(this);
        attachments.add(a);
    }

    public void removeAttachment(Attachment a) {
        if (a == null)
            return;
        attachments.remove(a);
        if (a.getMail() == this)
            a.setMail(null);
    }
}

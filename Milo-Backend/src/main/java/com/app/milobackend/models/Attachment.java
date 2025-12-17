package com.app.milobackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;


@Entity
@Table(name = "attachments")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Attachment{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;
    private Long size;

    // cascade = ALL: Saving this Attachment automatically saves the Content
    // fetch = LAZY: Loading this Attachment DOES NOT load the Content
    // optional = false: Every attachment must have content
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "content_id", nullable = false, unique = true)
    @JsonIgnore // (affects serialization, not persistence)
    private AttachmentContent content;

    // Many Attachments belong to one Mail.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mail_id")
    @JsonBackReference // When serializing Attachment, the full Mail object will be omitted (breaking the loop)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Mail mail;

    public Attachment(String fileName, String fileType, byte[] data) {
        this.name = fileName;
        this.type = fileType;
        this.size = data != null ? (long) data.length : 0L; // Store size at creation time

        // Create the content object and link them
        this.content = new AttachmentContent(data);
        this.content.setAttachment(this);
    }

    public Attachment(String fileName, String fileType) {
        this.name = fileName;
        this.type = fileType;
        this.size = 0L; // Store size at creation time

        // Create the content object and link them
        this.content = new AttachmentContent();
        this.content.setAttachment(this);
    }
}

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
public class Attachment{

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;

//    @Column(name = "data", columnDefinition="BYTEA")
//    private byte[] data;

    // cascade = ALL: Saving this Attachment automatically saves the Content
    // fetch = LAZY: Loading this Attachment DOES NOT load the Content (Speed!)
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

        // Create the content object and link them
        this.content = new AttachmentContent(data);
        this.content.setAttachment(this);
    }
}

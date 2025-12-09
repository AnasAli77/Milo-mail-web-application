package com.app.milobackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "attachments")
@Data
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;

    @Column(name = "data", columnDefinition="BYTEA")
    private byte[] data;

    // Many Attachments belong to one Mail.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "mail_id")
    @JsonBackReference // When serializing Attachment, the full Mail object will be omitted (breaking the loop)
    private Mail mail;

    public Attachment() {}

    public Attachment(String fileName, String fileType, byte[] data) {
        this.name = fileName;
        this.type = fileType;
        this.data = data;
    }
}

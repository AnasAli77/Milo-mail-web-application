package com.app.milobackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;


@Entity
@Table(name = "attachments")
@Getter @Setter
public class Attachment implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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

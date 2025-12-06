package com.app.milobackend.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachments")
@Data
public class Attachment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String type;

    @Lob
    @Column(name = "data", columnDefinition="BYTEA")
    private byte[] data;

    public Attachment() {}

    public Attachment(String fileName, String fileType, byte[] data) {
        this.name = fileName;
        this.type = fileType;
        this.data = data;
    }
}

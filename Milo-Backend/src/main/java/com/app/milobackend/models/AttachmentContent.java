package com.app.milobackend.models;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "attachment_contents")
@Getter @Setter
@NoArgsConstructor
public class AttachmentContent {

    @Id
    @Column(name = "attachment_id")
    private Long id;

    // The actual binary data (The Heavy Part)
    @Column(name = "data", columnDefinition="BYTEA")
    private byte[] data;

    // Link back to the metadata parent
    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // CRITICAL: This says "My ID is exactly the same as my parent's ID"
    @JoinColumn(name = "attachment_id")
    private Attachment attachment;

    public AttachmentContent(byte[] data) {
        this.data = data;
    }
}
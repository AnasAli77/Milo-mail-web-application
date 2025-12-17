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
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        private Long id;

        // The actual binary data
        @Column(name = "data", columnDefinition="BYTEA")
        private byte[] data;

        // Link back to the metadata parent
        @OneToOne(mappedBy = "content")
        private Attachment attachment;

        public AttachmentContent(byte[] data) {
            this.data = data;
        }
    }
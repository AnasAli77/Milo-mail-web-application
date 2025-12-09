package com.app.milobackend.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Instant createdAt = Instant.now();

    private String name;

    // One Folder can have many Mails.
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  // Mails within this folder WILL be serialized
    private List<Mail> mails = new ArrayList<>();

    public void addMail(Mail m) {
        if (m == null) return;
        mails.add(m);

    }

    public void removeMail(Mail m) {
        if (m == null) return;
        mails.remove(m);

    }
}

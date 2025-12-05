package com.app.milobackend.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Folder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private UUID id;

    private Instant createdAt = Instant.now();

    private String name;

    @JsonManagedReference
    List<Mail> mails = new ArrayList<>();

    public void addMail(Mail m) {
        if (m == null) return;
        mails.add(m);
        m.setFolder(this);
    }

    public void removeMail(Mail m) {
        if (m == null) return;
        mails.remove(m);
        m.setFolder(null);
    }
}

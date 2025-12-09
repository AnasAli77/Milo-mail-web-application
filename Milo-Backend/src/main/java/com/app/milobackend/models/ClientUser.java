package com.app.milobackend.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;


@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
@Table(name="users")
public class ClientUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(columnDefinition = "TEXT")
    private String passwordHash;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt =  LocalDateTime.now(ZoneId.of("Africa/Cairo"));

    @OneToMany(cascade = CascadeType.ALL)
    @JsonManagedReference
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

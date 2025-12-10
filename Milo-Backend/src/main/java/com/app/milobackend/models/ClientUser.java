package com.app.milobackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
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
    @Column(unique = true, nullable = false)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String passwordHash;

    @Column(columnDefinition = "TIMESTAMP")
    private LocalDateTime createdAt =  LocalDateTime.now(ZoneId.of("Africa/Cairo"));

   // --- Relationships ---

    // Mails sent by this user
    @OneToMany(mappedBy = "sender")
    @JsonIgnore // Prevent infinite recursion
    private List<Mail> sentMails = new ArrayList<>();

    // Mails received by this user
    @ManyToMany(mappedBy = "receivers")
    @JsonIgnore // Prevent infinite recursion
    private List<Mail> receivedMails = new ArrayList<>();


    public void addSentMail(Mail m) {
        if (m == null) return;
        sentMails.add(m);

    }

    public void removeSentMail(Mail m) {
        if (m == null) return;
        sentMails.remove(m);

    }
    public void addReceivedMail(Mail m) {
        if (m == null) return;
        receivedMails.add(m);

    }

    public void removeReceivedMail(Mail m) {
        if (m == null) return;
        receivedMails.remove(m);

    }
}

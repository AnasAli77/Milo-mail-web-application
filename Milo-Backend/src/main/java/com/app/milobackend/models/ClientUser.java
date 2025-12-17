package com.app.milobackend.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


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

    // Mails sent by this user
    @OneToMany(mappedBy = "sender")
    @JsonIgnore // Prevent infinite recursion
    private List<Mail> sentMails = new ArrayList<>();

    // Mails received by this user (single receiver per mail)
    @OneToMany(mappedBy = "receiver")
    @JsonIgnore // Prevent infinite recursion
    private List<Mail> receivedMails = new ArrayList<>();

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private Set<Folder> folders = new HashSet<>();

    public void addFolder(Folder folder) {
        folders.add(folder);
    }

    public void removeFolder(Folder folder) {
        folders.remove(folder);
    }

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

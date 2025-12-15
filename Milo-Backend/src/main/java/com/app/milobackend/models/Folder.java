package com.app.milobackend.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

@ToString
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter @Setter
public class Folder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime createdAt = LocalDateTime.now(ZoneId.of("Africa/Cairo"));

    private String name;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private ClientUser user; // The owner of this folder

    // One Folder can have many Mails.
    @OneToMany(mappedBy = "folder", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference  // Mails within this folder WILL be serialized
//    @ToString.Exclude
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

package com.app.milobackend.models;

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
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String email;

    @Column(columnDefinition = "TEXT")
    private String passwordHash;
    private Instant createdAt =  Instant.now();

    @OneToMany(cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<Mail> mails = new ArrayList<>();
}

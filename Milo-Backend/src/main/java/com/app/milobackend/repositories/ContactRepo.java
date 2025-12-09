package com.app.milobackend.repositories;

import com.app.milobackend.models.Contact;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


@Repository
public interface ContactRepo extends JpaRepository<Contact, Long> {

}

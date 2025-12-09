package com.app.milobackend.repositories;

import com.app.milobackend.models.ClientUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepo extends JpaRepository<ClientUser, Long> {

    ClientUser findByEmail(String email);
}

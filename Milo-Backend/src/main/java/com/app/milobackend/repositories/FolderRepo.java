package com.app.milobackend.repositories;

import com.app.milobackend.models.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FolderRepo extends JpaRepository<Folder,Long> {
    Folder findByNameAndUserEmail(String name, String email);

    List<Folder> findAllByUserEmail(String email);
}

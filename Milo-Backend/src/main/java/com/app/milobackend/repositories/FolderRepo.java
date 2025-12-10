package com.app.milobackend.repositories;

import com.app.milobackend.models.Folder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FolderRepo extends JpaRepository<Folder,Long> {
    Folder findByName(String name);
}

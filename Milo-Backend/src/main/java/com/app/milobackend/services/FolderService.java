package com.app.milobackend.services;

import com.app.milobackend.models.Folder;
import com.app.milobackend.repositories.FolderRepo;
import jakarta.mail.FolderNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class FolderService {

    @Autowired
    private FolderRepo folderRepo;

    public Folder createFolder(String folderName) {
        Folder folder = new Folder();
        folder.setName(folderName);
        return folderRepo.save(folder);
    }

    public boolean exists(Folder f) {
        Folder folder = folderRepo.findByName(f.getName());
        return (folder != null);
    }

    public void removeFolder(String name) {
        Folder folder = folderRepo.findByName(name);
        folderRepo.delete(folder);
    }

    public void renameFolder(String oldName, String newName) {
        Folder f = folderRepo.findByName(oldName);
        f.setName(newName);
        folderRepo.save(f);
    }
}

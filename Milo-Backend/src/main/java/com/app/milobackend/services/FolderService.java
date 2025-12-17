package com.app.milobackend.services;

import com.app.milobackend.models.ClientUser;
import com.app.milobackend.models.Folder;
import com.app.milobackend.repositories.FolderRepo;
import com.app.milobackend.repositories.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FolderService {

    @Autowired
    private FolderRepo folderRepo;

    @Autowired
    private UserRepo userRepo;

    public String getCurrentUserEmail() {
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null) {
            return authentication.getName();
        }
        return null;
    }

    public String[] getFolderNames() {
        List<Folder> folders = folderRepo.findAllByUserEmail(getCurrentUserEmail());
        String[] folderNames = new String[folders.size()];
        for (int i = 0; i < folders.size(); i++) {
            folderNames[i] = folders.get(i).getName();
        }
        return folderNames;
    }

    public Folder createFolder(String folderName) {
        String userEmail = getCurrentUserEmail();
        int counter = 0;
        String addedName = "";
        while (folderRepo.findByNameAndUserEmail(folderName + addedName, userEmail) != null) {
            counter++;
            addedName = " (" + counter + ")";
        }

        String uniqueName = folderName + addedName;
        ClientUser user = userRepo.findByEmail(userEmail);
        Folder folder = new Folder();
        folder.setName(uniqueName);
        folder.setUser(user);
        user.addFolder(folder);
        return folderRepo.save(folder);
    }

    // used to create the 4 default folders for the user
    public void createFolderWithUser(String folderName, ClientUser user) {
        Folder folder = new Folder();
        folder.setName(folderName);
        folder.setUser(user); // Critical: Link folder to user
        user.addFolder(folder);
        folderRepo.save(folder);
    }

    public void removeFolder(String name) {
        String email = getCurrentUserEmail();
        if (email == null) {
            throw new RuntimeException("User not authenticated");
        }
        Folder folder = folderRepo.findByNameAndUserEmail(name, email);
        if (folder != null) {
            folderRepo.delete(folder);
        }
    }

    public void renameFolder(String oldName, String newName) {
        String email = getCurrentUserEmail();
        if (email == null) {
            throw new RuntimeException("User not authenticated");
        }
        Folder folder = folderRepo.findByNameAndUserEmail(oldName, email);
        if (folder != null) {
            folder.setName(newName);
            folderRepo.save(folder);
        }
    }
}

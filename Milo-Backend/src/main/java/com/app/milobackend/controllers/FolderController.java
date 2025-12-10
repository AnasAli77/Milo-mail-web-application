package com.app.milobackend.controllers;

import com.app.milobackend.models.Folder;
import com.app.milobackend.services.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/folders")
public class FolderController {

    @Autowired
    private FolderService folderService;

    @PostMapping
    public ResponseEntity<Map<String, Object>> createFolder(@RequestBody String folderName) {
        Folder folder = folderService.createFolder(folderName);
        Map<String, Object> map = new HashMap<>();
        map.put("status", HttpStatus.CREATED);
        map.put("folder", folder);
        return ResponseEntity.status(HttpStatus.CREATED).body(map);
    }

    @DeleteMapping("{name}")
    public void deleteFolder(@PathVariable("name") String name) {
        folderService.removeFolder(name);

    }

    @PutMapping("{oldName}")
    public void updateFolder(@PathVariable("oldName") String oldName, @RequestBody String name) {
        folderService.renameFolder(oldName, name);
    }
}

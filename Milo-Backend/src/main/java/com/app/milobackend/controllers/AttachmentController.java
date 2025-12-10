package com.app.milobackend.controllers;

import com.app.milobackend.models.Attachment;
import com.app.milobackend.services.AttachmentService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@CrossOrigin(origins={"http://localhost:4200"})
@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from attachment";
    }

    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<ByteArrayResource> download(@PathVariable Long attachmentId) {
        Attachment attachment = attachmentService.getFile(attachmentId);

        return ResponseEntity.ok()
                // Tell the browser this is a file download
                .contentType(MediaType.parseMediaType(attachment.getType()))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + attachment.getName() + "\"")
                .body(new ByteArrayResource(attachment.getData()));
    }

//    @PostMapping("/upload")
//    public ResponseEntity<Attachment> upload(@RequestParam("file") MultipartFile file) throws IOException {
//        System.out.println("============== File uploaded: " + file.getOriginalFilename());
//        Attachment attachment = attachmentService.storeFile(file);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(attachment);
//    }

}

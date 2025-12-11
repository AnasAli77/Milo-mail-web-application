package com.app.milobackend.controllers;

import com.app.milobackend.models.Attachment;
import com.app.milobackend.services.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.UUID;

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
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable Long id) {
        byte[] data = attachmentService.getAttachmentData(id);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"file_" + id + "\"")
                .body(data);
    }

//    @PostMapping("/upload")
//    public ResponseEntity<Attachment> upload(@RequestParam("file") MultipartFile file) throws IOException {
//        System.out.println("============== File uploaded: " + file.getOriginalFilename());
//        Attachment attachment = attachmentService.storeFile(file);
//
//        return ResponseEntity.status(HttpStatus.CREATED).body(attachment);
//    }

}

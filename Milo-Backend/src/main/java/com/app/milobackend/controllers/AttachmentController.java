package com.app.milobackend.controllers;

import com.app.milobackend.services.AttachmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    @Autowired
    private AttachmentService attachmentService;

    @GetMapping("/hello")
    public String hello() {
        return "Hello from attachment";
    }

    @GetMapping("/download/{attachmentName}")
    public ResponseEntity<byte[]> downloadAttachment(@PathVariable("attachmentName") String name) {
        byte[] data = attachmentService.getAttachmentData(name);

        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .header(HttpHeaders.CONTENT_DISPOSITION, STR."attachment; filename=\"file_\{name}\"")
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

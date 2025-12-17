package com.app.milobackend.controllers;

import com.app.milobackend.models.Attachment;
import com.app.milobackend.services.AttachmentService;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {

    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @GetMapping("/hello")
    public String hello() {
        return "Hello from attachment";
    }

    @GetMapping("/download/{attachmentId}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable("attachmentId") Long id) {
        Attachment att = attachmentService.getAttachmentData(id);

        InputStreamResource resource = new InputStreamResource(
                new ByteArrayInputStream(att.getContent().getData())
        );

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + att.getName() + "\"")
                .contentType(MediaType.parseMediaType(att.getType()))
                .body(resource);
    }
}

package com.app.milobackend.controllers;

import com.app.milobackend.services.AttachmentService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
    public Map<String, Object> downloadAttachment(@PathVariable("attachmentId") Long id) {
        String data = attachmentService.getAttachmentData(id);

        System.out.println(data);
        Map<String, Object> response = new HashMap<>();
        response.put("data", data);

        return response;
    }
}

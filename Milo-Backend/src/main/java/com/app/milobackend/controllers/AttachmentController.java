package com.app.milobackend.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/attachment")
public class AttachmentController {
    @GetMapping("/hello")
    public String hello() {
        return "Hello from attachment";
    }
}

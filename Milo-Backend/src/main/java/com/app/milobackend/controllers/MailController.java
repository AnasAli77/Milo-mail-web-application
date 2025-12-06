package com.app.milobackend.controllers;

import com.app.milobackend.services.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {
    private final MailService mailService;

    @DeleteMapping("/delete/{id}")
    public void deleteMail (@PathVariable String id){
        mailService.delete(id);
    }


}

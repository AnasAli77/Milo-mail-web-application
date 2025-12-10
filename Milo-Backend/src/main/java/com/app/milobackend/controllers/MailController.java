package com.app.milobackend.controllers;

import com.app.milobackend.dtos.FilterDTO;
import com.app.milobackend.models.Mail;
import com.app.milobackend.services.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins={"http://localhost:4200"})
@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {
    @Autowired
    private final MailService mailService;

    @DeleteMapping("/delete/{id}")
    public void deleteMail (@PathVariable String id)
    {
        mailService.delete(id);

    }
    @GetMapping("/sort")
    public List<Mail> getSortedMails(@RequestParam String  sortBy){
        return mailService.getSortedMails(sortBy);
    }
    @PostMapping("/filter")
    public List<Mail> getFilteredMails(@RequestParam FilterDTO filterDTO){
        return mailService.Filter(filterDTO);
    }



}

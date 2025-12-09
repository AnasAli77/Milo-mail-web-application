package com.app.milobackend.controllers;

import com.app.milobackend.dtos.FilterDTO;
import com.app.milobackend.dtos.MailDTO;
import com.app.milobackend.models.Mail;
import com.app.milobackend.services.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {
    @Autowired
    private final MailService mailService;

    @DeleteMapping("/delete/{id}")
    public void deleteMail (@PathVariable Long id)
    {
        mailService.deleteMail(id);

    }

    @PostMapping("/add")
    public void addMail (@ModelAttribute MailDTO dto)
    {
        Mail mail= mailService.mapMailDTOtoMail(dto);
        mailService.AddMail(mail);
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

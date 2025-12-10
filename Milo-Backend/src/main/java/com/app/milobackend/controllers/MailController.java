package com.app.milobackend.controllers;

import com.app.milobackend.dtos.FilterDTO;
import com.app.milobackend.dtos.MailDTO;
import com.app.milobackend.mappers.MailMapper;
import com.app.milobackend.models.Folder;
import com.app.milobackend.models.Mail;
import com.app.milobackend.services.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/mail")
@RequiredArgsConstructor
public class MailController {
    @Autowired
    private final MailService mailService;
    @Autowired
    private MailMapper mailMapper;

    @DeleteMapping("/delete/{id}")
    public void deleteMail (@PathVariable Long id)
    {
        mailService.deleteMail(id);

    }

    @PostMapping("/send")
    public Map<String, Object> addMail (@RequestBody MailDTO dto)
    {
        System.out.println("Mail received: " + dto.toString());
        String message = "";
        try {
            mailService.saveMail(dto);
            message = "Mail has been saved successfully";
        } catch (RuntimeException e) {
            message = e.getMessage();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("message", message);
        return response;
    }
    @GetMapping("/sort")
    public List<Mail> getSortedMails(@RequestParam String  sortBy){
        return mailService.getSortedMails(sortBy);
    }
    @PostMapping("/filter")
    public List<Mail> getFilteredMails(@RequestParam FilterDTO filterDTO){
        return mailService.Filter(filterDTO);
    }

    @PutMapping("/star/{mailId}")
    public void toggleStarredMail(@PathVariable Long mailId){
        mailService.toggleStarredMail(mailId);
    }

    @GetMapping("/{folderName}")
    public Page<MailDTO> getMails(@PathVariable String folderName, @RequestParam("page") Integer page, @RequestParam("size") Integer size) {
        Page<MailDTO> mails = mailService.getMailsByFolder(folderName, page, size);
        return mails;
    }
}

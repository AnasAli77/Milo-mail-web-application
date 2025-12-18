package com.app.milobackend.controllers;

import com.app.milobackend.dtos.FilterDTO;
import com.app.milobackend.dtos.SearchDTO;
import com.app.milobackend.dtos.MailDTO;
import com.app.milobackend.services.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/mail")
public class MailController {
    private final MailService mailService;

    @Autowired
    public MailController(MailService mailService) {
        this.mailService = mailService;
    }

    @PostMapping(value = "/send", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addMail(
            @RequestPart("mail") MailDTO dto,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        System.err.println("Mail to save received: " + dto.toString());

        Map<String, Object> response = new HashMap<>();
        try {
            mailService.saveMail(dto, files);
            response.put("success", true);
            response.put("message", "Mail has been saved successfully");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            String errorMsg = e.getMessage();
            response.put("message", errorMsg);

            // Extract invalid emails for frontend highlighting
            if (errorMsg != null && errorMsg.contains("do not exist:")) {
                String emailsPart = errorMsg.substring(errorMsg.indexOf("do not exist:") + 14);
                String[] invalidEmailsArray = emailsPart.split(",");
                List<String> invalidEmails = new ArrayList<>();
                for (String email : invalidEmailsArray) {
                    invalidEmails.add(email.trim());
                }
                response.put("invalidEmails", invalidEmails);
            }

            e.printStackTrace();
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/{folderName}")
    public Page<MailDTO> getMails(@PathVariable String folderName, @RequestParam("page") Integer page,
            @RequestParam("size") Integer size) {
        return mailService.getMailsByFolder(folderName, page, size);
    }

    @DeleteMapping("/delete/{id}")
    public void deleteMail(@PathVariable Long id) {
        mailService.deleteMail(id);
    }

    @PutMapping(value = "/update", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public void updateMail(
            @RequestPart("mail") MailDTO mailDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {
        System.err.println("Mail to update received: " + mailDTO.toString());
        try {
            mailService.updateMail(mailDTO, files);
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    @PutMapping("/read/{mailId}")
    public void readMail(@PathVariable Long mailId) {
        mailService.markMailAsRead(mailId);
    }

    @PutMapping("/star/{mailId}")
    public void toggleStarredMail(@PathVariable Long mailId) {
        mailService.toggleStarredMail(mailId);
    }

    @PutMapping("/move")
    public void moveMails(@RequestBody Map<String, Object> mailIds_folder) {
        mailService.moveMailsToFolder(mailIds_folder);
    }

    @GetMapping("/filter")
    public Page<MailDTO> getFilteredMails(@RequestParam(required = false) String body,
            @RequestParam(required = false) String sender,
            @RequestParam(required = false) String subject,
            @RequestParam(required = false) String priority,
            @RequestParam(required = false) String hasAttachment,
            @RequestParam(required = false) String day,
            @RequestParam(required = false) String month,
            @RequestParam(required = false) String year,
            @RequestParam(defaultValue = "0") int pageNumber,
            @RequestParam(defaultValue = "9") int pageSize) {
        Map<String, String> map = new HashMap<>();
        if (body != null)
            map.put("body", body);
        if (sender != null)
            map.put("sender", sender);
        if (subject != null)
            map.put("subject", subject);
        if (priority != null)
            map.put("priority", priority);
        if (hasAttachment != null)
            map.put("hasAttachment", hasAttachment);
        if (day != null)
            map.put("day", day);
        if (month != null)
            map.put("month", month);
        if (year != null)
            map.put("year", year);

        FilterDTO filterDTO = new FilterDTO();
        filterDTO.setKeys(map);

        return mailService.Filter(filterDTO, pageNumber, pageSize);
    }

    @GetMapping("/search/{searchBy}")
    public Page<MailDTO> searchEmails(@PathVariable("searchBy") String searchBy, @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize) {
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCriteria(List.of("body", "subject", "receiver", "sender"));
        searchDTO.setWord(searchBy);
        return mailService.Search(searchDTO, pageNumber, pageSize);

    }

    @GetMapping("/sort/{folderName}/{sortBy}")
    public Page<MailDTO> getSortedMails(@PathVariable("sortBy") String sortBy,
            @PathVariable("folderName") String folderName, @RequestParam Integer pageNumber,
            @RequestParam Integer pageSize) {
        return mailService.getSortedMails(sortBy, folderName, pageNumber, pageSize);
    }
}

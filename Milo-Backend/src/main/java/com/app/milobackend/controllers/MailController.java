package com.app.milobackend.controllers;

import com.app.milobackend.dtos.FilterDTO;
import com.app.milobackend.dtos.SearchDTO;
import com.app.milobackend.dtos.MailDTO;
import com.app.milobackend.mappers.MailMapper;
import com.app.milobackend.services.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

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

    @PutMapping("/update")
    public void updateMail(@RequestBody MailDTO mailDTO)
    {
        mailService.updateMail(mailDTO);
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
    @GetMapping("/sort/{folderName}/{sortBy}")
    public Page<MailDTO> getSortedMails(@PathVariable("sortBy") String  sortBy,@PathVariable("folderName") String folderName , @RequestParam Integer pageNumber, @RequestParam Integer pageSize){
        return mailService.getSortedMails(sortBy, folderName, pageNumber, pageSize);
    }
    @GetMapping("/filter") // 8ayar deeh ya 3m markeb
    public Page<MailDTO> getFilteredMails(@RequestParam(required = false) String body,
                                          @RequestParam(required = false) String sender,
                                          @RequestParam(required = false) String subject,
                                          @RequestParam(required = false) String priority,
                                          @RequestParam(required = false) String hasAttachment,
                                          @RequestParam(required = false) String day,
                                          @RequestParam(required = false) String month,
                                          @RequestParam(required = false) String year,
                                          @RequestParam(defaultValue = "0") int pageNumber,
                                          @RequestParam(defaultValue = "9") int pageSize){
        Map<String, String> map = new HashMap<>();
        if (body != null) map.put("body", body);
        if (sender != null) map.put("sender", sender);
        if (subject != null) map.put("subject", subject);
        if (priority != null) map.put("priority", priority);
        if (hasAttachment != null) map.put("hasAttachment", hasAttachment);
        if (day != null) map.put("day", day);
        if (month != null) map.put("month", month);
        if (year != null) map.put("year", year);

        FilterDTO filterDTO = new FilterDTO();
        filterDTO.setKeys(map);

        return mailService.Filter(filterDTO, pageNumber, pageSize);
    }
    @GetMapping("/search/{searchBy}")
    public Page<MailDTO> searchEmails(@PathVariable("searchBy") String searchBy, @RequestParam Integer pageNumber, @RequestParam Integer pageSize){
        SearchDTO searchDTO = new SearchDTO();
        searchDTO.setCriteria(List.of("body", "subject","receiver","sender"));
        searchDTO.setWord(searchBy);
        return mailService.Search(searchDTO,pageNumber,pageSize);

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

    @PutMapping("/move")
    public void moveMails(@RequestBody Map<String, Object> mailIds_folder){
        mailService.moveMailsToFolder(mailIds_folder);
    }
}

package com.app.milobackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailDTO {
    private String sender;
    private String receiver;
    private String subject;
    private String body;
    private int priority;
    private List<MultipartFile> attachments;

}

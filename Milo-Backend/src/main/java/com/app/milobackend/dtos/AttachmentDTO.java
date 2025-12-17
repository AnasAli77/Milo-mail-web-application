package com.app.milobackend.dtos;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class AttachmentDTO { // needed when sending mails
    private Long id;
    private String fileName;
    private String fileType;
    private Long size; // File size in bytes
}
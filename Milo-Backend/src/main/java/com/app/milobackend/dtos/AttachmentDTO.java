package com.app.milobackend.dtos;

import lombok.Data;

@Data
public class AttachmentDTO { // only needed when sending mails
    private Long id;
    private String fileName;
    private String fileType;
    private String base64Content; // The file data as a string
    private Long size; // File size in bytes
}
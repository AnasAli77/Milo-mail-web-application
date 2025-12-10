package com.app.milobackend.dtos;

import lombok.Data;

@Data
public class AttachmentDTO {
    private String fileName;
    private String fileType;
    private String base64Content; // The file data as a string
}
package com.app.milobackend.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class MailDTO {
    private Long id;
    private String folder;
    private String sender;
    private String senderEmail;
    private List<String> receiverEmail;
    private String time;
    private String subject;
    private String body;
    private List<AttachmentDTO> attachments;
    private boolean read;
    private boolean active;
    private boolean starred;
    private boolean hasAttachment;
    private int priority;

}

package com.app.milobackend.services;

import com.app.milobackend.dtos.AttachmentDTO;
import com.app.milobackend.models.Attachment;
import com.app.milobackend.repositories.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.UUID;

@Service
public class AttachmentService {


    private final AttachmentRepository attachmentrepo;
    @Autowired
    public AttachmentService(AttachmentRepository attachmentrepo) {
        this.attachmentrepo = attachmentrepo;
    }

    public Attachment createAttachmentEntity(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        // Just create the object in memory. Do NOT save it yet.
        return new Attachment(
                fileName,
                file.getContentType(),
                file.getBytes()
        );
    }

    public Attachment getFile(Long fileId) {
        return attachmentrepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id: " + fileId));
    }

    public List<Attachment> convertDTOsToAttachments(List<AttachmentDTO> attachmentDTOs) {
        List<Attachment> attachments = new ArrayList<>();

        if (attachmentDTOs == null || attachmentDTOs.isEmpty()) return attachments;

        for (AttachmentDTO dto : attachmentDTOs) {
            // 1. Decode the Base64 String back to raw bytes
            byte[] decodedBytes = Base64.getDecoder().decode(dto.getBase64Content());

            // 2. Create the entity
            Attachment attachment = new Attachment(
                    dto.getFileName(),
                    dto.getFileType(),
                    decodedBytes
            );
            attachments.add(attachment);
        }
        return attachments;
    }
}

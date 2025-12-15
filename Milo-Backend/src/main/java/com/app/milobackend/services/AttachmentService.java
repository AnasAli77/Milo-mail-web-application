package com.app.milobackend.services;

import com.app.milobackend.dtos.AttachmentDTO;
import com.app.milobackend.models.Attachment;
import com.app.milobackend.models.AttachmentContent;
import com.app.milobackend.repositories.AttachmentContentRepo;
import com.app.milobackend.repositories.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class AttachmentService {


    @Autowired
    private AttachmentRepository attachmentrepo;

    @Autowired
    private AttachmentContentRepo contentRepo;

    @Transactional(readOnly = true)
    @Cacheable(value = "attachment", key = "_#id")
    public String getAttachmentData(Long id) throws RuntimeException {
        Attachment attachment = attachmentrepo.findById(id).orElse(null);
        if (attachment == null) {
            throw new RuntimeException("Attachment with id" + id + "is not found");
        }

        AttachmentContent content = attachment.getContent();

        byte[] data =  content.getData();
        return Base64.getEncoder().encodeToString(data);
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

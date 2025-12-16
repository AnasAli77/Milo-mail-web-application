package com.app.milobackend.services;

import com.app.milobackend.dtos.AttachmentDTO;
import com.app.milobackend.models.Attachment;
import com.app.milobackend.models.AttachmentContent;
import com.app.milobackend.repositories.AttachmentContentRepo;
import com.app.milobackend.repositories.AttachmentRepository;
import jakarta.annotation.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class AttachmentService {


    @Autowired
    private AttachmentRepository attachmentRepo;

    @Autowired
    private AttachmentContentRepo contentRepo;

    @Transactional(readOnly = true)
//    @Cacheable(value = "attachment", key = "#id")
    public Attachment getAttachmentData(Long id) throws RuntimeException {
        Attachment attachment = attachmentRepo.findById(id).orElse(null);
        if (attachment == null) {
            throw new RuntimeException("Attachment with id " + id + " is not found");
        }

        return attachment;
    }

    public List<Attachment> convertDTOsToAttachments(List<AttachmentDTO> attachmentDTOs, List<MultipartFile> files) throws IOException {

        for (AttachmentDTO attachmentDTO : attachmentDTOs) {
            System.out.println("AttachmentDTO: " + attachmentDTO.toString());
        }

        List<Attachment> attachments = new ArrayList<>();

        // Return empty list if no files provided
        if ((files == null || files.isEmpty()) && (attachmentDTOs == null || attachmentDTOs.isEmpty())) return attachments;

        if (files != null && !files.isEmpty()) {
            for (MultipartFile file : files) {
                // Create the entity from the MultipartFile
                Attachment attachment = new Attachment(
                        file.getOriginalFilename(),
                        file.getContentType(),
                        file.getBytes()
                );
                attachments.add(attachment);
            }
        }
        for  (AttachmentDTO attachmentDTO : attachmentDTOs) {
            if (attachmentDTO.getId() != null) {
                Attachment attachment = attachmentRepo.findById(attachmentDTO.getId()).orElse(null);
                if (attachment != null) {
                    attachments.add(attachment);
                }
            }
        }
        return attachments;
    }
}

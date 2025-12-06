package com.app.milobackend.services;

import com.app.milobackend.models.Attachment;
import com.app.milobackend.repositories.AttachmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class AttachmentService {

    private final AttachmentRepository attachmentrepo;

    public AttachmentService(AttachmentRepository attachmentrepo) {
        this.attachmentrepo = attachmentrepo;
    }

    public Attachment storeFile(MultipartFile file) throws IOException {
        String fileName = StringUtils.cleanPath(file.getOriginalFilename());

        Attachment fileAttachment = new Attachment(
                fileName,
                file.getContentType(),
                file.getBytes()
        );

        return attachmentrepo.save(fileAttachment);
    }

    public Attachment getFile(Long fileId) {
        return attachmentrepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found with id " + fileId));
    }
}

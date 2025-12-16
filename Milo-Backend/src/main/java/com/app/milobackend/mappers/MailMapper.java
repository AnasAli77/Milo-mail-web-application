package com.app.milobackend.mappers;

import com.app.milobackend.dtos.MailDTO;
import com.app.milobackend.models.Mail;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface MailMapper {
    Mail toEntity(MailDTO dto, List<MultipartFile> files) throws IOException;
    MailDTO toDTO(Mail entity);
}
package com.app.milobackend.mappers;

import com.app.milobackend.dtos.MailDTO;
import com.app.milobackend.models.Mail;

public interface MailMapper {
    Mail toEntity(MailDTO dto);
    MailDTO toDTO(Mail entity);
}
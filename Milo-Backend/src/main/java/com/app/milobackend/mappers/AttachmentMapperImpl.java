package com.app.milobackend.mappers;

import com.app.milobackend.dtos.AttachmentDTO;
import com.app.milobackend.models.Attachment;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class AttachmentMapperImpl implements AttachmentMapper {

    @Override
    public Attachment toEntity(AttachmentDTO dto) {
        return new Attachment(dto.getFileName(), dto.getFileType());
    }

    @Override
    public AttachmentDTO toDTO(Attachment entity) {
        if (entity == null) return null;

        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(entity.getId());
        dto.setFileName(entity.getName());
        dto.setFileType(entity.getType());
        dto.setSize(entity.getSize());

        return dto;
    }
}
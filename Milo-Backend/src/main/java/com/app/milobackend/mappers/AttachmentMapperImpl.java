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
//        if (dto == null) return null;

//        byte[] decodedBytes = null;
//        if (dto.getBase64Content() != null && !dto.getBase64Content().isEmpty()) {
//            try {
//                // Decode Base64 -> Byte[]
//                String cleanBase64 = dto.getBase64Content();
//                if (cleanBase64.contains(",")) {
//                    cleanBase64 = cleanBase64.split(",")[1];
//                }
//                decodedBytes = Base64.getDecoder().decode(cleanBase64);
//            } catch (IllegalArgumentException e) {
//                System.err.println("Failed to decode attachment: " + dto.getFileName());
//            }
//        }

        return new Attachment(dto.getFileName(), dto.getFileType());
    }

    @Override
    public AttachmentDTO toDTO(Attachment entity) {
        if (entity == null) return null;

        AttachmentDTO dto = new AttachmentDTO();
        dto.setId(entity.getId());
        dto.setFileName(entity.getName());
        dto.setFileType(entity.getType());
        // IMPORTANT: We do NOT map the content back to DTO here.
        // We want the list to be light. Content is fetched only via specific download endpoint.
//        dto.setBase64Content(null);
        
        // Use the cached size field (avoids lazy-loading the heavy content blob)
        dto.setSize(entity.getSize());

        return dto;
    }
}
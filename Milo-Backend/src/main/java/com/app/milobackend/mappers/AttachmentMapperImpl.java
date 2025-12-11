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
        if (dto == null) return null;

        byte[] decodedBytes = null;
        if (dto.getBase64Content() != null && !dto.getBase64Content().isEmpty()) {
            try {
                // Decode Base64 -> Byte[]
                // In Angular, files often come as "data:image/png;base64,iVBOR..."
                // You might need to split the string if it contains the header.
                String cleanBase64 = dto.getBase64Content();
                if (cleanBase64.contains(",")) {
                    cleanBase64 = cleanBase64.split(",")[1];
                }
                decodedBytes = Base64.getDecoder().decode(cleanBase64);
            } catch (IllegalArgumentException e) {
                System.err.println("Failed to decode attachment: " + dto.getFileName());
            }
        }

        // Use the constructor we made in Attachment.java to link them instantly
        return new Attachment(dto.getFileName(), dto.getFileType(), decodedBytes);
    }

    @Override
    public AttachmentDTO toDTO(Attachment entity) {
        if (entity == null) return null;

        AttachmentDTO dto = new AttachmentDTO();
        dto.setFileName(entity.getName());
        dto.setFileType(entity.getType());
        // IMPORTANT: We do NOT map the content back to DTO here.
        // We want the list to be light. Content is fetched only via specific download endpoint.
        dto.setBase64Content(null);

        return dto;
    }

    @Override
    public List<Attachment> toEntityList(List<AttachmentDTO> dtos) {
        if (dtos == null) return new ArrayList<>();
        return dtos.stream().map(this::toEntity).collect(Collectors.toList());
    }

    @Override
    public List<AttachmentDTO> toDTOList(List<Attachment> entities) { // Changed input from Set to List if needed, or convert
        if (entities == null) return new ArrayList<>();
        return entities.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
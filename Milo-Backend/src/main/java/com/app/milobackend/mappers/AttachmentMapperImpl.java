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
        if (dto == null) {
            return null;
        }

        byte[] decodedBytes = null;
        // Decode String (DTO) -> Bytes (Entity)
        if (dto.getBase64Content() != null && !dto.getBase64Content().isEmpty()) {
            try {
                decodedBytes = Base64.getDecoder().decode(dto.getBase64Content());
            } catch (IllegalArgumentException e) {
                throw new RuntimeException("Invalid Base64 content for file: " + dto.getFileName(), e);
            }
        }

        return new Attachment(
                dto.getFileName(),
                dto.getFileType(),
                decodedBytes
        );
    }

    @Override
    public AttachmentDTO toDTO(Attachment entity) {
        if (entity == null) {
            return null;
        }

        AttachmentDTO dto = new AttachmentDTO();
        dto.setFileName(entity.getName());
        dto.setFileType(entity.getType());

        // Encode Bytes (Entity) -> String (DTO)
        if (entity.getData() != null) {
            String encodedString = Base64.getEncoder().encodeToString(entity.getData());
            dto.setBase64Content(encodedString);
        }

        return dto;
    }

    @Override
    public List<Attachment> toEntityList(List<AttachmentDTO> dtos) {
        if (dtos == null) return new ArrayList<>();
        return dtos.stream()
                .map(this::toEntity)
                .collect(Collectors.toList());
    }

    @Override
    public List<AttachmentDTO> toDTOList(List<Attachment> entities) {
        if (entities == null) return new ArrayList<>();
        return entities.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
}
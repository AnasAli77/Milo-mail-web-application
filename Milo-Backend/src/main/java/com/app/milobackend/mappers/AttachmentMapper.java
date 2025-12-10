package com.app.milobackend.mappers;

import com.app.milobackend.dtos.AttachmentDTO;
import com.app.milobackend.models.Attachment;
import java.util.List;

public interface AttachmentMapper {
    Attachment toEntity(AttachmentDTO dto);
    AttachmentDTO toDTO(Attachment entity);

    // Helper methods for Lists (very useful for your Mail mapper)
    List<Attachment> toEntityList(List<AttachmentDTO> dtos);
    List<AttachmentDTO> toDTOList(List<Attachment> entities);
}
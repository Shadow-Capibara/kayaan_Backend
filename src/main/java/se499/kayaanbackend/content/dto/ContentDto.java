package se499.kayaanbackend.content.dto;

import lombok.Data;
import se499.kayaanbackend.content.enums.ContentType;

import java.time.LocalDateTime;

@Data
public class ContentDto {
    private Long contentId;
    private Long userId;
    private ContentType contentType;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;
}

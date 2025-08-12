package se499.kayaanbackend.Study_Group.dto;

import java.time.LocalDateTime;
import java.util.List;

public record ResourceResponse(
    Long id,
    String title,
    String description,
    String fileUrl,
    String mimeType,
    Long fileSize,
    List<String> tags,
    Integer uploaderId,
    LocalDateTime createdAt
) {}

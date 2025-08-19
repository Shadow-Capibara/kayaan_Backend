package se499.kayaanbackend.Study_Group.dto;

import java.time.LocalDateTime;

public record ResourceResponse(
    Long id,
    String title,
    String type,
    String preview,
    String fileUrl,
    String storagePath,
    String mimeType,
    String stats,
    Integer uploaderId,
    LocalDateTime createdAt
) {}

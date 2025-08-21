package se499.kayaanbackend.Study_Group.dto;

import java.time.LocalDateTime;

public record MessageResponse(
    Long id,
    Integer groupId,
    Integer userId,
    String userName,
    String content,
    String messageType,
    LocalDateTime createdAt,
    LocalDateTime updatedAt
) {}

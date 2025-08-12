package se499.kayaanbackend.Study_Group.dto;

import java.time.LocalDateTime;

public record MessageResponse(
    Long id,
    Integer senderId,
    String content,
    LocalDateTime createdAt
) {}

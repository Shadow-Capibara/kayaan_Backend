package se499.kayaanbackend.Study_Group.dto;

import java.time.LocalDateTime;
import java.util.List;

public record SharedContentResponse(
    Long id,
    String title,
    String description,
    String contentType,
    String contentData,
    List<String> tags,
    Integer uploaderId,
    LocalDateTime createdAt
) {}

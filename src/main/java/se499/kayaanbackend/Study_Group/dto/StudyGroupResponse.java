package se499.kayaanbackend.Study_Group.dto;

import java.time.LocalDateTime;

public record StudyGroupResponse(
    Integer id,
    String name,
    String description,
    Integer ownerId,
    LocalDateTime createdAt
) {}

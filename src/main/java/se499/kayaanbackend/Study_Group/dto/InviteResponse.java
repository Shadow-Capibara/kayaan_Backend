package se499.kayaanbackend.Study_Group.dto;

import java.time.LocalDateTime;

public record InviteResponse(
    String token,
    LocalDateTime expiresAt
) {}

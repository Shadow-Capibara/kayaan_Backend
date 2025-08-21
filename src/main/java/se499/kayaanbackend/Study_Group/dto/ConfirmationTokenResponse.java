package se499.kayaanbackend.Study_Group.dto;

import java.time.LocalDateTime;

public record ConfirmationTokenResponse(
    String confirmationToken,
    LocalDateTime expiresAt,
    String action,
    Long targetId,
    String message
) {}

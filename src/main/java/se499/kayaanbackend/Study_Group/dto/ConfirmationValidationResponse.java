package se499.kayaanbackend.Study_Group.dto;

public record ConfirmationValidationResponse(
    boolean isValid,
    String message,
    String action,
    Long targetId,
    String reason
) {}

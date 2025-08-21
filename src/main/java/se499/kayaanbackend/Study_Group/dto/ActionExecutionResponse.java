package se499.kayaanbackend.Study_Group.dto;

public record ActionExecutionResponse(
    boolean success,
    String message,
    String action,
    Long targetId,
    Object result
) {}

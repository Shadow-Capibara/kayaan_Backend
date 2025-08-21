package se499.kayaanbackend.Study_Group.dto;

public record MessageRequest(
    String content,
    String messageType // "text", "image", "file"
) {}

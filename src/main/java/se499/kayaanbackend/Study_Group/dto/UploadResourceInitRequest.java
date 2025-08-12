package se499.kayaanbackend.Study_Group.dto;

public record UploadResourceInitRequest(
    String fileName,
    String mimeType,
    Long size
) {}

package se499.kayaanbackend.Study_Group.dto;

import java.util.Map;

public record UploadResourceCompleteRequest(
    String title,
    String type,
    Map<String, Object> preview,
    Map<String, Object> stats,
    String storagePath,
    String mimeType,
    Long size
) {}

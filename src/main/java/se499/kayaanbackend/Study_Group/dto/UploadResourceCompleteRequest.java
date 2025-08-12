package se499.kayaanbackend.Study_Group.dto;

import java.util.List;

public record UploadResourceCompleteRequest(
    String fileName,
    String fileUrl,
    String title,
    String description,
    List<String> tags
) {}

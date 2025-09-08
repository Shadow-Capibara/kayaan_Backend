package se499.kayaanbackend.Study_Group.dto;

import java.util.List;

public record ShareContentRequest(
    String contentId,
    String title,
    String description,
    List<String> tags,
    String contentType,
    String contentData
) {}

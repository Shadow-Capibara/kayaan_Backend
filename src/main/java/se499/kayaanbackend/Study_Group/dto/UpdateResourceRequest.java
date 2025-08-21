package se499.kayaanbackend.Study_Group.dto;

import java.util.List;

public record UpdateResourceRequest(
    String title,
    String description,
    List<String> tags
) {}

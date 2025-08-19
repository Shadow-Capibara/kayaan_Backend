package se499.kayaanbackend.Study_Group.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public record UploadResourceInitRequest(
    String fileName,
    @JsonAlias({"contentType"}) String mimeType,
    Long size
) {}

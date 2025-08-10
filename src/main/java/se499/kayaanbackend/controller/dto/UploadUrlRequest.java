package se499.kayaanbackend.controller.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;

/**
 * Request DTO for avatar upload URL
 * Supports multiple field name variations for frontend compatibility
 */
public record UploadUrlRequest(
    @NotBlank(message = "fileName must not be blank")
    @JsonProperty("fileName")
    @JsonAlias({"filename", "name"})
    String fileName,
    
    @JsonProperty("contentType")
    @JsonAlias({"mimeType", "type"})
    String contentType
) {
    /**
     * Get content type with default fallback
     */
    public String getContentTypeOrDefault() {
        return (contentType != null && !contentType.isBlank()) 
            ? contentType 
            : "application/octet-stream";
    }
}

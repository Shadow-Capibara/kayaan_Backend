package se499.kayaanbackend.AI_Generate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIGeneratedContentDTO {
    
    private Long id;
    private Long generationRequestId;
    private Long userId;
    private String contentTitle;
    private String contentType;
    private String contentData; // JSON string
    private Integer contentVersion;
    private String supabaseFilePath;
    private Long fileSize;
    private Boolean isSaved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for enhanced display
    private String downloadUrl; // Signed URL for download
    private String previewUrl; // URL for preview
    private String generationStatus; // Status of the generation request
    private String userPrompt; // Original prompt used for generation
}

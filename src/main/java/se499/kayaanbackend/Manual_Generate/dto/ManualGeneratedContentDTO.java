package se499.kayaanbackend.Manual_Generate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * DTO for ManualGeneratedContent with JSON format
 * This mirrors AIGeneratedContentDTO structure for consistency
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManualGeneratedContentDTO {
    
    private Long id;
    private Long userId;
    private String username;
    private String contentTitle;
    private String contentType; // "flashcard", "quiz", "note"
    private String contentData; // JSON string
    private Integer contentVersion;
    private String subject;
    private String difficulty;
    private List<String> tags; // Parsed from comma-separated string
    private Boolean isSaved;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Additional fields for enhanced display
    private String displayFormat; // "structured", "raw", "preview"
    private Object parsedContent; // Parsed JSON object for frontend consumption
    private Long contentSize; // Size of content data in bytes
    private String contentSummary; // Brief summary of content
}

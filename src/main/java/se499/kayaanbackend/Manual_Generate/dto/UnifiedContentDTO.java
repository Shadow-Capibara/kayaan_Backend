package se499.kayaanbackend.Manual_Generate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Unified DTO for both AI and Manual content
 * Allows Frontend to display all content types consistently
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedContentDTO {
    
    private String id; // Format: "ai-123" or "manual-quiz-456"
    private String title;
    private String contentType; // "quiz", "flashcard", "note"
    private String source; // "ai", "manual"
    private String content; // JSON string in AI-compatible format
    
    // Timestamps
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Manual content specific fields
    private String difficulty;
    private String subject;
    private List<String> tags;
    private String createdByUsername;
    
    // For AI content compatibility
    private Long aiRequestId; // Only for AI content
    private String supabaseFilePath; // Only for AI content
    private Long fileSize; // Only for AI content
    private Boolean isSaved; // Only for AI content
}

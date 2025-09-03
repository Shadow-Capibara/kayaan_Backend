package se499.kayaanbackend.Manual_Generate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * Response wrapper for unified content API
 * Provides pagination and metadata for Frontend
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UnifiedContentResponse {
    
    private List<UnifiedContentDTO> content;
    private int totalElements;
    private int currentPage;
    private int totalPages;
    private int size;
    
    // Summary information
    private ContentSummary summary;
    
    // Error handling
    private String error;
    private boolean success;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentSummary {
        private int totalAiContent;
        private int totalManualContent;
        private int totalQuizzes;
        private int totalFlashcards;
        private int totalNotes;
    }
}

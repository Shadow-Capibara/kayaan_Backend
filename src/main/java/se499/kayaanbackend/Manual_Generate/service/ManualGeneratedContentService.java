package se499.kayaanbackend.Manual_Generate.service;

import se499.kayaanbackend.Manual_Generate.dto.CreateManualContentDTO;
import se499.kayaanbackend.Manual_Generate.dto.ManualGeneratedContentDTO;
import se499.kayaanbackend.AI_Generate.entity.ContentType;

import java.util.List;

/**
 * Service interface for ManualGeneratedContent operations
 * This service handles JSON-based manual content creation and management
 */
public interface ManualGeneratedContentService {
    
    /**
     * Create new manual content with JSON format
     */
    ManualGeneratedContentDTO createContent(CreateManualContentDTO dto, String username);
    
    /**
     * Get manual content by ID
     */
    ManualGeneratedContentDTO getContentById(Long id, String username);
    
    /**
     * Update existing manual content
     */
    ManualGeneratedContentDTO updateContent(Long id, CreateManualContentDTO dto, String username);
    
    /**
     * Soft delete manual content
     */
    void deleteContent(Long id, String username);
    
    /**
     * Get all manual content for user
     */
    List<ManualGeneratedContentDTO> getAllContentForUser(String username);
    
    /**
     * Get manual content by type for user
     */
    List<ManualGeneratedContentDTO> getContentByTypeForUser(String username, ContentType contentType);
    
    /**
     * Search manual content by title
     */
    List<ManualGeneratedContentDTO> searchContentByTitle(String username, String searchTerm);
    
    /**
     * Get content statistics for user
     */
    ContentStatistics getContentStatistics(String username);
    
    /**
     * Convert legacy content to JSON format
     */
    ManualGeneratedContentDTO convertLegacyContent(String contentType, Object legacyContent, String username);
    
    /**
     * Validate JSON content structure
     */
    boolean validateJsonContent(String contentType, String jsonContent);
    
    /**
     * Content statistics inner class
     */
    public static class ContentStatistics {
        private long totalContent;
        private long quizCount;
        private long noteCount;
        private long flashcardCount;
        
        // Constructors
        public ContentStatistics() {}
        
        public ContentStatistics(long totalContent, long quizCount, long noteCount, long flashcardCount) {
            this.totalContent = totalContent;
            this.quizCount = quizCount;
            this.noteCount = noteCount;
            this.flashcardCount = flashcardCount;
        }
        
        // Getters and Setters
        public long getTotalContent() { return totalContent; }
        public void setTotalContent(long totalContent) { this.totalContent = totalContent; }
        
        public long getQuizCount() { return quizCount; }
        public void setQuizCount(long quizCount) { this.quizCount = quizCount; }
        
        public long getNoteCount() { return noteCount; }
        public void setNoteCount(long noteCount) { this.noteCount = noteCount; }
        
        public long getFlashcardCount() { return flashcardCount; }
        public void setFlashcardCount(long flashcardCount) { this.flashcardCount = flashcardCount; }
    }
}

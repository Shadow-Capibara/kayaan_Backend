package se499.kayaanbackend.AI_Generate.service;

import se499.kayaanbackend.AI_Generate.dto.*;
import se499.kayaanbackend.AI_Generate.entity.AIGenerationRequest.GenerationStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Service interface for AI Generation feature
 * Covers all use cases: UC-19 to UC-27
 */
public interface AIGenerationService {
    
    // UC-19: Write a generation prompt
    // UC-20: Select a generation output prompt
    // UC-21: Submit a generation request
    /**
     * Create a new AI generation request
     * @param dto Generation request data
     * @param userId User ID
     * @return Generation request ID
     */
    Long createGenerationRequest(CreateGenerationRequestDTO dto, Long userId);
    
    // UC-22: (AI) Generate content
    /**
     * Start AI content generation for a request
     * @param requestId Generation request ID
     * @param userId User ID
     * @return CompletableFuture for async generation
     */
    CompletableFuture<String> generateContent(Long requestId, Long userId);
    
    // UC-23: Preview generation content
    /**
     * Preview generated content before saving
     * @param dto Preview request data
     * @param userId User ID
     * @return Preview data
     */
    Object previewContent(PreviewRequestDTO dto, Long userId);
    
    // UC-24: View generation status
    /**
     * Get generation request status
     * @param requestId Generation request ID
     * @param userId User ID
     * @return Generation status
     */
    GenerationStatusDTO getGenerationStatus(Long requestId, Long userId);
    
    /**
     * Get all generation requests for a user
     * @param userId User ID
     * @param pageable Pagination
     * @return Page of generation requests
     */
    Page<GenerationStatusDTO> getUserGenerationRequests(Long userId, Pageable pageable);
    
    // UC-25: Cancel generation
    /**
     * Cancel a generation request
     * @param requestId Generation request ID
     * @param userId User ID
     * @return True if cancelled successfully
     */
    boolean cancelGeneration(Long requestId, Long userId);
    
    // UC-26: Retry generation
    /**
     * Retry a failed generation request
     * @param requestId Generation request ID
     * @param userId User ID
     * @return New generation request ID
     */
    Long retryGeneration(Long requestId, Long userId);
    
    // UC-27: Save generated content
    /**
     * Save generated content
     * @param dto Save content data
     * @param userId User ID
     * @return Saved content ID
     */
    Long saveGeneratedContent(SaveContentDTO dto, Long userId);
    
    /**
     * Get saved content for a user
     * @param userId User ID
     * @param pageable Pagination
     * @return Page of saved content
     */
    Page<AIGeneratedContentDTO> getUserSavedContent(Long userId, Pageable pageable);
    
    /**
     * Download saved content
     * @param contentId Content ID
     * @param userId User ID
     * @return Content data
     */
    String downloadContent(Long contentId, Long userId);
    
    /**
     * Delete saved content
     * @param contentId Content ID
     * @param userId User ID
     * @return True if deleted successfully
     */
    boolean deleteSavedContent(Long contentId, Long userId);
    
    // Template Management
    /**
     * Create a new prompt template
     * @param dto Template data
     * @param userId User ID
     * @return Template ID
     */
    Long createPromptTemplate(AIPromptTemplateDTO dto, Long userId);
    
    /**
     * Get user's prompt templates
     * @param userId User ID
     * @param pageable Pagination
     * @return Page of templates
     */
    Page<AIPromptTemplateDTO> getUserTemplates(Long userId, Pageable pageable);
    
    /**
     * Get public templates
     * @param pageable Pagination
     * @return Page of public templates
     */
    Page<AIPromptTemplateDTO> getPublicTemplates(Pageable pageable);
    
    /**
     * Update a prompt template
     * @param templateId Template ID
     * @param dto Updated template data
     * @param userId User ID
     * @return Updated template
     */
    AIPromptTemplateDTO updatePromptTemplate(Long templateId, AIPromptTemplateDTO dto, Long userId);
    
    /**
     * Delete a prompt template
     * @param templateId Template ID
     * @param userId User ID
     * @return True if deleted successfully
     */
    boolean deletePromptTemplate(Long templateId, Long userId);
    
    // Utility Methods
    /**
     * Check if user can create new generation request (rate limiting)
     * @param userId User ID
     * @return True if allowed
     */
    boolean canCreateGenerationRequest(Long userId);
    
    /**
     * Get user's generation statistics
     * @param userId User ID
     * @return Statistics data
     */
    Object getUserGenerationStats(Long userId);
    
    /**
     * Clean up old/failed generation requests
     * @param daysOld Number of days old to clean up
     * @return Number of cleaned up requests
     */
    int cleanupOldRequests(int daysOld);
}

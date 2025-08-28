package se499.kayaanbackend.AI_Generate.service.impl;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.AI_Generate.dto.AIGeneratedContentDTO;
import se499.kayaanbackend.AI_Generate.dto.AIPromptTemplateDTO;
import se499.kayaanbackend.AI_Generate.dto.CreateGenerationRequestDTO;
import se499.kayaanbackend.AI_Generate.dto.GenerationStatusDTO;
import se499.kayaanbackend.AI_Generate.dto.PreviewRequestDTO;
import se499.kayaanbackend.AI_Generate.dto.SaveContentDTO;
import se499.kayaanbackend.AI_Generate.entity.AIGeneratedContent;
import se499.kayaanbackend.AI_Generate.entity.AIGenerationRequest;
import se499.kayaanbackend.AI_Generate.entity.AIGenerationRequest.GenerationStatus;
import se499.kayaanbackend.AI_Generate.entity.ContentType;
import se499.kayaanbackend.AI_Generate.repository.AIGeneratedContentRepository;
import se499.kayaanbackend.AI_Generate.repository.AIGenerationRequestRepository;
import se499.kayaanbackend.AI_Generate.service.AIGenerationRateLimitService;
import se499.kayaanbackend.AI_Generate.service.AIGenerationService;
import se499.kayaanbackend.AI_Generate.service.ContentTypeValidationService;
import se499.kayaanbackend.AI_Generate.service.OpenAIService;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.security.user.UserDao;

/**
 * Implementation of AIGenerationService
 * Handles all AI content generation business logic
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AIGenerationServiceImpl implements AIGenerationService {

    private final AIGenerationRequestRepository generationRequestRepository;
    private final AIGeneratedContentRepository generatedContentRepository;
    private final UserDao userDao;
    private final OpenAIService openAIService;
    private final ContentTypeValidationService contentTypeValidationService;
    private final AIGenerationRateLimitService rateLimitService;

    @Override
    @Transactional
    public Long createGenerationRequest(CreateGenerationRequestDTO dto, Long userId) {
        try {
            log.info("Creating generation request for user: {}, format: {}", userId, dto.getOutputFormat());
            
            // Validate user exists
            User user = userDao.findById(userId.intValue())
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            
            // Validate content type
            ContentType contentType = contentTypeValidationService.toContentType(dto.getOutputFormat());
            
            // Check rate limits
            if (!rateLimitService.canMakeRequest(userId.toString())) {
                throw new RuntimeException("Rate limit exceeded. Please try again later.");
            }
            
            // Create generation request
            AIGenerationRequest request = AIGenerationRequest.builder()
                .user(user)
                .promptText(dto.getPromptText())
                .outputFormat(contentType)
                .maxRetries(dto.getMaxRetries() != null ? dto.getMaxRetries() : 3)
                .status(GenerationStatus.PENDING)
                .progress(0)  // Set default progress
                .retryCount(0)  // Set default retry count
                .createdAt(LocalDateTime.now())
                .build();
            
            AIGenerationRequest savedRequest = generationRequestRepository.save(request);
            
            log.info("Generation request created successfully with ID: {}", savedRequest.getId());
            return savedRequest.getId();
            
        } catch (Exception e) {
            log.error("Error creating generation request", e);
            throw new RuntimeException("Failed to create generation request: " + e.getMessage());
        }
    }

    @Override
    public CompletableFuture<String> generateContent(Long requestId, Long userId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                log.info("Starting content generation for request: {}, user: {}", requestId, userId);
                
                // Get generation request
                AIGenerationRequest request = generationRequestRepository.findById(requestId)
                    .orElseThrow(() -> new RuntimeException("Generation request not found: " + requestId));
                
                // Verify user ownership
                if (!request.getUser().getId().equals(userId.intValue())) {
                    throw new RuntimeException("Access denied to generation request: " + requestId);
                }
                
                // Update status to processing
                request.setStatus(GenerationStatus.PROCESSING);
                request.setStartedAt(LocalDateTime.now());
                generationRequestRepository.save(request);
                
                // Generate content using OpenAI
                String generatedContent = openAIService.generateContent(
                    request.getPromptText(),
                    request.getOutputFormat().getValue(),
                    "", // additionalContext - not stored in entity yet
                    userId.toString()
                );
                
                // Save generated content to database
                AIGeneratedContent content = AIGeneratedContent.builder()
                    .generationRequest(request)
                    .user(request.getUser())
                    .contentTitle("Generated " + request.getOutputFormat().getValue())
                    .contentType(request.getOutputFormat())
                    .contentData(generatedContent)
                    .isSaved(true)
                    .createdAt(LocalDateTime.now())
                    .build();
                
                generatedContentRepository.save(content);
                log.info("Generated content saved to database for request: {}", requestId);
                
                // Update status to completed
                request.setStatus(GenerationStatus.COMPLETED);
                request.setCompletedAt(LocalDateTime.now());
                generationRequestRepository.save(request);
                
                log.info("Content generation completed for request: {}", requestId);
                return generatedContent;
                
            } catch (Exception e) {
                log.error("Error generating content for request: {}", requestId, e);
                
                // Update status to failed
                try {
                    AIGenerationRequest request = generationRequestRepository.findById(requestId).orElse(null);
                    if (request != null) {
                        request.setStatus(GenerationStatus.FAILED);
                        request.setErrorMessage(e.getMessage());
                        generationRequestRepository.save(request);
                    }
                } catch (Exception updateError) {
                    log.error("Error updating request status to failed", updateError);
                }
                
                throw new RuntimeException("Content generation failed: " + e.getMessage());
            }
        });
    }

    @Override
    public Object previewContent(PreviewRequestDTO dto, Long userId) {
        try {
            log.info("Previewing content for user: {}, type: {}", userId, dto.getContentType());
            
            // Validate content type
            ContentType contentType = contentTypeValidationService.toContentType(dto.getContentType());
            
            // Check rate limits for preview
            if (!rateLimitService.canMakeRequest(userId.toString())) {
                throw new RuntimeException("Rate limit exceeded. Please try again later.");
            }
            
            // Generate preview using OpenAI (shorter version)
            String previewContent = openAIService.generateContent(
                "Create a brief preview of: " + dto.getContentData(),
                contentType.getValue(),
                "This is a preview request",
                userId.toString()
            );
            
            return previewContent;
            
        } catch (Exception e) {
            log.error("Error previewing content", e);
            throw new RuntimeException("Failed to preview content: " + e.getMessage());
        }
    }

    @Override
    public GenerationStatusDTO getGenerationStatus(Long requestId, Long userId) {
        try {
            log.info("Getting generation status for request: {}, user: {}", requestId, userId);
            
            AIGenerationRequest request = generationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Generation request not found: " + requestId));
            
            // Verify user ownership
            if (!request.getUser().getId().equals(userId.intValue())) {
                throw new RuntimeException("Access denied to generation request: " + requestId);
            }
            
            return GenerationStatusDTO.builder()
                .id(request.getId())
                .userId(request.getUser().getId().longValue())
                .promptText(request.getPromptText())
                .outputFormat(request.getOutputFormat().getValue())
                .status(request.getStatus())
                .progress(calculateProgress(request))
                .errorMessage(request.getErrorMessage())
                .createdAt(request.getCreatedAt())
                .startedAt(request.getStartedAt())
                .completedAt(request.getCompletedAt())
                .retryCount(request.getRetryCount())
                .maxRetries(request.getMaxRetries())
                .build();
            
        } catch (Exception e) {
            log.error("Error getting generation status", e);
            throw new RuntimeException("Failed to get generation status: " + e.getMessage());
        }
    }

    @Override
    public Object getGenerationProgress(Long requestId, Long userId) {
        GenerationStatusDTO status = getGenerationStatus(requestId, userId);
        return status; // For now, return the same as status
    }

    @Override
    public Page<GenerationStatusDTO> getUserGenerationRequests(Long userId, Pageable pageable) {
        try {
            log.info("Getting generation requests for user: {}", userId);
            
            Page<AIGenerationRequest> requests = generationRequestRepository
                .findByUser_IdOrderByCreatedAtDesc(userId.intValue(), pageable);
            
            return requests.map(request -> GenerationStatusDTO.builder()
                .id(request.getId())
                .userId(request.getUser().getId().longValue())
                .promptText(request.getPromptText())
                .outputFormat(request.getOutputFormat().getValue())
                .status(request.getStatus())
                .progress(calculateProgress(request))
                .errorMessage(request.getErrorMessage())
                .createdAt(request.getCreatedAt())
                .startedAt(request.getStartedAt())
                .completedAt(request.getCompletedAt())
                .retryCount(request.getRetryCount())
                .maxRetries(request.getMaxRetries())
                .build());
            
        } catch (Exception e) {
            log.error("Error getting user generation requests", e);
            throw new RuntimeException("Failed to get generation requests: " + e.getMessage());
        }
    }

    @Override
    public boolean cancelGeneration(Long requestId, Long userId) {
        try {
            log.info("Cancelling generation request: {}, user: {}", requestId, userId);
            
            AIGenerationRequest request = generationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Generation request not found: " + requestId));
            
            // Verify user ownership
            if (!request.getUser().getId().equals(userId.intValue())) {
                throw new RuntimeException("Access denied to generation request: " + requestId);
            }
            
            // Only allow cancellation of pending or processing requests
            if (request.getStatus() == GenerationStatus.PENDING || 
                request.getStatus() == GenerationStatus.PROCESSING) {
                
                request.setStatus(GenerationStatus.CANCELLED);
                request.setCompletedAt(LocalDateTime.now());
                generationRequestRepository.save(request);
                
                log.info("Generation request cancelled successfully: {}", requestId);
                return true;
            }
            
            return false;
            
        } catch (Exception e) {
            log.error("Error cancelling generation request", e);
            throw new RuntimeException("Failed to cancel generation: " + e.getMessage());
        }
    }

    @Override
    public Long retryGeneration(Long requestId, Long userId) {
        try {
            log.info("Retrying generation request: {}, user: {}", requestId, userId);
            
            AIGenerationRequest originalRequest = generationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Generation request not found: " + requestId));
            
            // Verify user ownership
            if (!originalRequest.getUser().getId().equals(userId.intValue())) {
                throw new RuntimeException("Access denied to generation request: " + requestId);
            }
            
            // Check if retry is allowed
            if (originalRequest.getRetryCount() >= originalRequest.getMaxRetries()) {
                throw new RuntimeException("Maximum retries exceeded for request: " + requestId);
            }
            
            // Create new generation request
            AIGenerationRequest newRequest = AIGenerationRequest.builder()
                .user(originalRequest.getUser())
                .promptText(originalRequest.getPromptText())
                .outputFormat(originalRequest.getOutputFormat())
                .maxRetries(originalRequest.getMaxRetries())
                .retryCount(originalRequest.getRetryCount() + 1)
                .status(GenerationStatus.PENDING)
                .createdAt(LocalDateTime.now())
                .build();
            
            AIGenerationRequest savedRequest = generationRequestRepository.save(newRequest);
            
            log.info("Generation retry created with ID: {}", savedRequest.getId());
            return savedRequest.getId();
            
        } catch (Exception e) {
            log.error("Error retrying generation request", e);
            throw new RuntimeException("Failed to retry generation: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public Long saveGeneratedContent(SaveContentDTO dto, Long userId) {
        try {
            log.info("Saving generated content for user: {}, type: {}", userId, dto.getContentType());
            
            // Validate user exists
            User user = userDao.findById(userId.intValue())
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
            
            // Validate content type
            ContentType contentType = contentTypeValidationService.toContentType(dto.getContentType());
            
            // Create generated content entity
            AIGeneratedContent content = AIGeneratedContent.builder()
                .user(user)
                .contentTitle(dto.getContentTitle())
                .contentType(contentType)
                .contentData(dto.getContentData())
                .isSaved(true)
                .createdAt(LocalDateTime.now())
                .build();
            
            AIGeneratedContent savedContent = generatedContentRepository.save(content);
            
            log.info("Generated content saved successfully with ID: {}", savedContent.getId());
            return savedContent.getId();
            
        } catch (Exception e) {
            log.error("Error saving generated content", e);
            throw new RuntimeException("Failed to save content: " + e.getMessage());
        }
    }

    @Override
    public Page<AIGeneratedContentDTO> getUserSavedContent(Long userId, Pageable pageable) {
        try {
            log.info("Getting saved content for user: {}", userId);
            
            Page<AIGeneratedContent> content = generatedContentRepository
                .findByUser_IdOrderByCreatedAtDesc(userId.intValue(), pageable);
            
            return content.map(this::toAIGeneratedContentDTO);
            
        } catch (Exception e) {
            log.error("Error getting user saved content", e);
            throw new RuntimeException("Failed to get saved content: " + e.getMessage());
        }
    }

    @Override
    public String downloadContent(Long contentId, Long userId) {
        try {
            log.info("Downloading content: {}, user: {}", contentId, userId);
            
            AIGeneratedContent content = generatedContentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found: " + contentId));
            
            // Verify user ownership
            if (!content.getUser().getId().equals(userId.intValue())) {
                throw new RuntimeException("Access denied to content: " + contentId);
            }
            
            return content.getContentData();
            
        } catch (Exception e) {
            log.error("Error downloading content", e);
            throw new RuntimeException("Failed to download content: " + e.getMessage());
        }
    }

    @Override
    public boolean deleteSavedContent(Long contentId, Long userId) {
        try {
            log.info("Deleting saved content: {}, user: {}", contentId, userId);
            
            AIGeneratedContent content = generatedContentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found: " + contentId));
            
            // Verify user ownership
            if (!content.getUser().getId().equals(userId.intValue())) {
                throw new RuntimeException("Access denied to content: " + contentId);
            }
            
            generatedContentRepository.delete(content);
            
            log.info("Saved content deleted successfully: {}", contentId);
            return true;
            
        } catch (Exception e) {
            log.error("Error deleting saved content", e);
            throw new RuntimeException("Failed to delete content: " + e.getMessage());
        }
    }

    // Template Management Methods (Placeholder implementations)
    @Override
    public Long createPromptTemplate(AIPromptTemplateDTO dto, Long userId) {
        throw new RuntimeException("Template management not yet implemented");
    }

    @Override
    public Page<AIPromptTemplateDTO> getUserTemplates(Long userId, Pageable pageable) {
        throw new RuntimeException("Template management not yet implemented");
    }

    @Override
    public Page<AIPromptTemplateDTO> getPublicTemplates(Pageable pageable) {
        throw new RuntimeException("Template management not yet implemented");
    }

    @Override
    public AIPromptTemplateDTO updatePromptTemplate(Long templateId, AIPromptTemplateDTO dto, Long userId) {
        throw new RuntimeException("Template management not yet implemented");
    }

    @Override
    public boolean deletePromptTemplate(Long templateId, Long userId) {
        throw new RuntimeException("Template management not yet implemented");
    }

    @Override
    public boolean canCreateGenerationRequest(Long userId) {
        return rateLimitService.canMakeRequest(userId.toString());
    }

    @Override
    public Object getUserGenerationStats(Long userId) {
        // Placeholder implementation
        return Map.of(
            "totalRequests", 0,
            "completedRequests", 0,
            "failedRequests", 0,
            "averageGenerationTime", "N/A"
        );
    }

    @Override
    public int cleanupOldRequests(int daysOld) {
        // Placeholder implementation
        log.info("Cleanup old requests older than {} days", daysOld);
        return 0;
    }

    // Helper methods
    private int calculateProgress(AIGenerationRequest request) {
        switch (request.getStatus()) {
            case PENDING: return 0;
            case PROCESSING: return 50;
            case COMPLETED: return 100;
            case FAILED:
            case CANCELLED: return 100;
            default: return 0;
        }
    }

    private AIGeneratedContentDTO toAIGeneratedContentDTO(AIGeneratedContent content) {
        return AIGeneratedContentDTO.builder()
            .id(content.getId())
            .userId(content.getUser().getId().longValue())
            .contentTitle(content.getContentTitle())
            .contentType(content.getContentType().getValue())
            .contentData(content.getContentData())
            .isSaved(content.getIsSaved())
            .createdAt(content.getCreatedAt())
            .build();
    }
}

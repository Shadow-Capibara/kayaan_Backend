package se499.kayaanbackend.AI_Generate.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se499.kayaanbackend.AI_Generate.dto.*;
import se499.kayaanbackend.AI_Generate.entity.*;
import se499.kayaanbackend.AI_Generate.repository.*;
import se499.kayaanbackend.AI_Generate.service.AIGenerationService;
import se499.kayaanbackend.AI_Generate.service.AISupabaseService;
import se499.kayaanbackend.AI_Generate.service.OpenAIService;
import se499.kayaanbackend.security.user.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Implementation of AIGenerationService
 * Covers all use cases: UC-19 to UC-27
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AIGenerationServiceImpl implements AIGenerationService {
    
    private final AIGenerationRequestRepository generationRequestRepository;
    private final AIPromptTemplateRepository templateRepository;
    private final AIGeneratedContentRepository contentRepository;
    private final AISupabaseService supabaseService;
    private final OpenAIService openAIService;
    
    // Rate limiting and progress tracking
    private final ConcurrentHashMap<Long, AtomicInteger> userRequestCounts = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<Long, Integer> requestProgress = new ConcurrentHashMap<>();
    
    // Rate limiting constants
    private static final int MAX_REQUESTS_PER_HOUR = 5;
    private static final int MAX_PREVIEWS_PER_MINUTE = 3;
    
    @Override
    public Long createGenerationRequest(CreateGenerationRequestDTO dto, Long userId) {
        log.info("Creating generation request for user: {}, format: {}", userId, dto.getOutputFormat());
        
        // Check rate limiting
        if (!canCreateGenerationRequest(userId)) {
            throw new RuntimeException("Rate limit exceeded. Maximum 5 requests per hour allowed.");
        }
        
        // Create generation request
        AIGenerationRequest request = AIGenerationRequest.builder()
                .user(User.builder().id(userId.intValue()).build())
                .promptText(dto.getPromptText())
                .outputFormat(dto.getOutputFormat())
                .status(AIGenerationRequest.GenerationStatus.PENDING)
                .progress(0)
                .maxRetries(dto.getMaxRetries())
                .build();
        
        AIGenerationRequest savedRequest = generationRequestRepository.save(request);
        
        // Update rate limiting counter
        userRequestCounts.computeIfAbsent(userId, k -> new AtomicInteger(0))
                .incrementAndGet();
        
        log.info("Generation request created successfully: {}", savedRequest.getId());
        return savedRequest.getId();
    }
    
    @Override
    public CompletableFuture<String> generateContent(Long requestId, Long userId) {
        log.info("Starting content generation for request: {}, user: {}", requestId, userId);
        
        return CompletableFuture.supplyAsync(() -> {
            try {
                // Get generation request
                AIGenerationRequest request = generationRequestRepository.findById(requestId)
                        .orElseThrow(() -> new RuntimeException("Generation request not found"));
                
                // Verify user ownership
                if (!request.getUser().getId().equals(userId.intValue())) {
                    throw new RuntimeException("Access denied to generation request");
                }
                
                // Update status to processing
                request.setStatus(AIGenerationRequest.GenerationStatus.PROCESSING);
                request.setStartedAt(LocalDateTime.now());
                request.setProgress(10);
                generationRequestRepository.save(request);
                
                // Start progress tracking
                startProgressTracking(requestId);
                
                // Generate content using OpenAI
                String generatedContent = openAIService.generateContent(
                    request.getPromptText(),
                    request.getOutputFormat(),
                    null // additionalContext
                );
                
                // Update progress to completed
                request.setStatus(AIGenerationRequest.GenerationStatus.COMPLETED);
                request.setProgress(100);
                request.setCompletedAt(LocalDateTime.now());
                generationRequestRepository.save(request);
                
                // Stop progress tracking
                stopProgressTracking(requestId);
                
                log.info("Content generation completed for request: {}", requestId);
                return generatedContent;
                
            } catch (Exception e) {
                log.error("Content generation failed for request: {}", requestId, e);
                
                // Update status to failed
                try {
                    AIGenerationRequest request = generationRequestRepository.findById(requestId).orElse(null);
                    if (request != null) {
                        request.setStatus(AIGenerationRequest.GenerationStatus.FAILED);
                        request.setErrorMessage(e.getMessage());
                        request.setProgress(0);
                        generationRequestRepository.save(request);
                    }
                } catch (Exception updateException) {
                    log.error("Failed to update request status to failed", updateException);
                }
                
                // Stop progress tracking
                stopProgressTracking(requestId);
                
                throw new RuntimeException("Content generation failed: " + e.getMessage());
            }
        });
    }
    
    @Override
    public Object previewContent(PreviewRequestDTO dto, Long userId) {
        log.info("Previewing content for request: {}, user: {}", dto.getGenerationRequestId(), userId);
        
        // Check rate limiting for previews
        if (!canPreviewContent(userId)) {
            throw new RuntimeException("Preview rate limit exceeded. Maximum 3 previews per minute allowed.");
        }
        
        // Verify request ownership
        AIGenerationRequest request = generationRequestRepository.findById(dto.getGenerationRequestId())
                .orElseThrow(() -> new RuntimeException("Generation request not found"));
        
        if (!request.getUser().getId().equals(userId.intValue())) {
            throw new RuntimeException("Access denied to generation request");
        }
        
        // Create preview object
        PreviewResponse preview = PreviewResponse.builder()
                .contentTitle(dto.getContentTitle())
                .contentType(dto.getContentType())
                .contentData(dto.getContentData())
                .previewFormat(dto.getPreviewFormat())
                .includeMetadata(dto.getIncludeMetadata())
                .generatedAt(LocalDateTime.now())
                .build();
        
        log.info("Content preview generated successfully");
        return preview;
    }
    
    @Override
    public GenerationStatusDTO getGenerationStatus(Long requestId, Long userId) {
        log.info("Getting generation status for request: {}, user: {}", requestId, userId);
        
        AIGenerationRequest request = generationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Generation request not found"));
        
        if (!request.getUser().getId().equals(userId.intValue())) {
            throw new RuntimeException("Access denied to generation request");
        }
        
        // Get current progress
        Integer currentProgress = requestProgress.get(requestId);
        if (currentProgress == null) {
            currentProgress = request.getProgress();
        }
        
        // Build status DTO
        GenerationStatusDTO statusDTO = GenerationStatusDTO.builder()
                .id(request.getId())
                .userId(request.getUser().getId().longValue())
                .promptText(request.getPromptText())
                .outputFormat(request.getOutputFormat())
                .status(request.getStatus())
                .progress(currentProgress)
                .errorMessage(request.getErrorMessage())
                .retryCount(request.getRetryCount())
                .maxRetries(request.getMaxRetries())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .startedAt(request.getStartedAt())
                .completedAt(request.getCompletedAt())
                .canCancel(canCancelGeneration(request))
                .canRetry(canRetryGeneration(request))
                .build();
        
        return statusDTO;
    }
    
    @Override
    public Page<GenerationStatusDTO> getUserGenerationRequests(Long userId, Pageable pageable) {
        log.info("Getting generation requests for user: {}", userId);
        
        Page<AIGenerationRequest> requests = generationRequestRepository.findByUser_IdOrderByCreatedAtDesc(userId.intValue(), pageable);
        
        return requests.map(request -> {
            Integer currentProgress = requestProgress.get(request.getId());
            if (currentProgress == null) {
                currentProgress = request.getProgress();
            }
            
            return GenerationStatusDTO.builder()
                    .id(request.getId())
                    .userId(request.getUser().getId().longValue())
                    .promptText(request.getPromptText())
                    .outputFormat(request.getOutputFormat())
                    .status(request.getStatus())
                    .progress(currentProgress)
                    .errorMessage(request.getErrorMessage())
                    .retryCount(request.getRetryCount())
                    .maxRetries(request.getMaxRetries())
                    .createdAt(request.getCreatedAt())
                    .updatedAt(request.getUpdatedAt())
                    .startedAt(request.getStartedAt())
                    .completedAt(request.getCompletedAt())
                    .canCancel(canCancelGeneration(request))
                    .canRetry(canRetryGeneration(request))
                    .build();
        });
    }
    
    @Override
    public boolean cancelGeneration(Long requestId, Long userId) {
        log.info("Cancelling generation request: {}, user: {}", requestId, userId);
        
        AIGenerationRequest request = generationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Generation request not found"));
        
        if (!request.getUser().getId().equals(userId.intValue())) {
            throw new RuntimeException("Access denied to generation request");
        }
        
        if (!canCancelGeneration(request)) {
            throw new RuntimeException("Cannot cancel generation request in current status");
        }
        
        request.setStatus(AIGenerationRequest.GenerationStatus.CANCELLED);
        request.setProgress(0);
        generationRequestRepository.save(request);
        
        // Stop progress tracking
        stopProgressTracking(requestId);
        
        log.info("Generation request cancelled successfully: {}", requestId);
        return true;
    }
    
    @Override
    public Long retryGeneration(Long requestId, Long userId) {
        log.info("Retrying generation request: {}, user: {}", requestId, userId);
        
        AIGenerationRequest request = generationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Generation request not found"));
        
        if (!request.getUser().getId().equals(userId.intValue())) {
            throw new RuntimeException("Access denied to generation request");
        }
        
        if (!canRetryGeneration(request)) {
            throw new RuntimeException("Cannot retry generation request in current status");
        }
        
        // Create new request with same parameters
        CreateGenerationRequestDTO retryDto = CreateGenerationRequestDTO.builder()
                .promptText(request.getPromptText())
                .outputFormat(request.getOutputFormat())
                .maxRetries(request.getMaxRetries())
                .build();
        
        Long newRequestId = createGenerationRequest(retryDto, userId);
        
        log.info("Generation request retry created: {}", newRequestId);
        return newRequestId;
    }
    
    @Override
    public Long saveGeneratedContent(SaveContentDTO dto, Long userId) {
        log.info("Saving generated content for request: {}, user: {}", dto.getGenerationRequestId(), userId);
        
        // Verify request ownership
        AIGenerationRequest request = generationRequestRepository.findById(dto.getGenerationRequestId())
                .orElseThrow(() -> new RuntimeException("Generation request not found"));
        
        if (!request.getUser().getId().equals(userId.intValue())) {
            throw new RuntimeException("Access denied to generation request");
        }
        
        // Upload to Supabase if requested
        String supabaseFilePath = null;
        if (dto.getSaveToSupabase()) {
            String fileName = dto.getCustomFileName();
            if (fileName == null || fileName.trim().isEmpty()) {
                fileName = String.format("content_%s_%s.json", request.getId(), System.currentTimeMillis());
            }
            supabaseFilePath = supabaseService.uploadContent(dto.getContentData(), fileName, userId);
        }
        
        // Create content entity
        AIGeneratedContent content = AIGeneratedContent.builder()
                .generationRequest(request)
                .user(User.builder().id(userId.intValue()).build())
                .contentTitle(dto.getContentTitle())
                .contentType(dto.getContentType())
                .contentData(dto.getContentData())
                .contentVersion(1)
                .supabaseFilePath(supabaseFilePath)
                .fileSize(supabaseService.getFileSize(supabaseFilePath))
                .isSaved(true)
                .build();
        
        AIGeneratedContent savedContent = contentRepository.save(content);
        
        log.info("Generated content saved successfully: {}", savedContent.getId());
        return savedContent.getId();
    }
    
    @Override
    public Page<AIGeneratedContentDTO> getUserSavedContent(Long userId, Pageable pageable) {
        log.info("Getting saved content for user: {}", userId);
        
        Page<AIGeneratedContent> content = contentRepository.findByUser_IdOrderByCreatedAtDesc(userId.intValue(), pageable);
        
        return content.map(this::convertToContentDTO);
    }
    
    @Override
    public String downloadContent(Long contentId, Long userId) {
        log.info("Downloading content: {}, user: {}", contentId, userId);
        
        AIGeneratedContent content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        
        if (!content.getUser().getId().equals(userId.intValue())) {
            throw new RuntimeException("Access denied to content");
        }
        
        if (content.getSupabaseFilePath() != null) {
            return supabaseService.downloadContent(content.getSupabaseFilePath());
        } else {
            return content.getContentData();
        }
    }
    
    @Override
    public boolean deleteSavedContent(Long contentId, Long userId) {
        log.info("Deleting saved content: {}, user: {}", contentId, userId);
        
        AIGeneratedContent content = contentRepository.findById(contentId)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        
        if (!content.getUser().getId().equals(userId.intValue())) {
            throw new RuntimeException("Access denied to content");
        }
        
        // Delete from Supabase if exists
        if (content.getSupabaseFilePath() != null) {
            supabaseService.deleteContent(content.getSupabaseFilePath());
        }
        
        contentRepository.delete(content);
        
        log.info("Content deleted successfully: {}", contentId);
        return true;
    }
    
    @Override
    public Long createPromptTemplate(AIPromptTemplateDTO dto, Long userId) {
        log.info("Creating prompt template for user: {}", userId);
        
        AIPromptTemplate template = AIPromptTemplate.builder()
                .user(User.builder().id(userId.intValue()).build())
                .templateName(dto.getTemplateName())
                .templateDescription(dto.getTemplateDescription())
                .promptText(dto.getPromptText())
                .outputFormat(dto.getOutputFormat())
                .isPublic(dto.getIsPublic())
                .isActive(dto.getIsActive())
                .usageCount(0)
                .build();
        
        AIPromptTemplate savedTemplate = templateRepository.save(template);
        
        log.info("Prompt template created successfully: {}", savedTemplate.getId());
        return savedTemplate.getId();
    }
    
    @Override
    public Page<AIPromptTemplateDTO> getUserTemplates(Long userId, Pageable pageable) {
        log.info("Getting templates for user: {}", userId);
        
        Page<AIPromptTemplate> templates = templateRepository.findByUser_IdOrderByCreatedAtDesc(userId.intValue(), pageable);
        
        return templates.map(this::convertToTemplateDTO);
    }
    
    @Override
    public Page<AIPromptTemplateDTO> getPublicTemplates(Pageable pageable) {
        log.info("Getting public templates");
        
        List<AIPromptTemplate> templates = templateRepository.findByIsPublicTrueAndIsActiveTrue();
        
        // Convert List to Page manually
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), templates.size());
        
        if (start > templates.size()) {
            return Page.empty(pageable);
        }
        
        List<AIPromptTemplate> pageContent = templates.subList(start, end);
        Page<AIPromptTemplate> page = new org.springframework.data.domain.PageImpl<>(pageContent, pageable, templates.size());
        
        return page.map(this::convertToTemplateDTO);
    }
    
    @Override
    public AIPromptTemplateDTO updatePromptTemplate(Long templateId, AIPromptTemplateDTO dto, Long userId) {
        log.info("Updating prompt template: {}, user: {}", templateId, userId);
        
        AIPromptTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        
        if (!template.getUser().getId().equals(userId.intValue())) {
            throw new RuntimeException("Access denied to template");
        }
        
        template.setTemplateName(dto.getTemplateName());
        template.setTemplateDescription(dto.getTemplateDescription());
        template.setPromptText(dto.getPromptText());
        template.setOutputFormat(dto.getOutputFormat());
        template.setIsPublic(dto.getIsPublic());
        template.setIsActive(dto.getIsActive());
        
        AIPromptTemplate updatedTemplate = templateRepository.save(template);
        
        log.info("Template updated successfully: {}", templateId);
        return convertToTemplateDTO(updatedTemplate);
    }
    
    @Override
    public boolean deletePromptTemplate(Long templateId, Long userId) {
        log.info("Deleting prompt template: {}, user: {}", templateId, userId);
        
        AIPromptTemplate template = templateRepository.findById(templateId)
                .orElseThrow(() -> new RuntimeException("Template not found"));
        
        if (!template.getUser().getId().equals(userId.intValue())) {
            throw new RuntimeException("Access denied to template");
        }
        
        templateRepository.delete(template);
        
        log.info("Template deleted successfully: {}", templateId);
        return true;
    }
    
    @Override
    public boolean canCreateGenerationRequest(Long userId) {
        AtomicInteger count = userRequestCounts.get(userId);
        return count == null || count.get() < MAX_REQUESTS_PER_HOUR;
    }
    
    @Override
    public Object getUserGenerationStats(Long userId) {
        log.info("Getting generation stats for user: {}", userId);
        
        long totalRequests = generationRequestRepository.countByUser_Id(userId.intValue());
        long completedRequests = generationRequestRepository.countByUser_IdAndStatus(userId.intValue(), AIGenerationRequest.GenerationStatus.COMPLETED);
        long failedRequests = generationRequestRepository.countByUser_IdAndStatus(userId.intValue(), AIGenerationRequest.GenerationStatus.FAILED);
        long savedContent = contentRepository.countByUser_IdAndIsSavedTrue(userId.intValue());
        
        return GenerationStats.builder()
                .totalRequests(totalRequests)
                .completedRequests(completedRequests)
                .failedRequests(failedRequests)
                .successRate(totalRequests > 0 ? (double) completedRequests / totalRequests : 0.0)
                .savedContent(savedContent)
                .build();
    }
    
    @Override
    public int cleanupOldRequests(int daysOld) {
        log.info("Cleaning up requests older than {} days", daysOld);
        
        LocalDateTime cutoff = LocalDateTime.now().minusDays(daysOld);
        List<AIGenerationRequest> oldRequests = generationRequestRepository
                .findByStatusAndCreatedAtBefore(AIGenerationRequest.GenerationStatus.PENDING, cutoff);
        
        int cleanedCount = 0;
        for (AIGenerationRequest request : oldRequests) {
            try {
                request.setStatus(AIGenerationRequest.GenerationStatus.CANCELLED);
                request.setErrorMessage("Auto-cancelled due to age");
                generationRequestRepository.save(request);
                cleanedCount++;
            } catch (Exception e) {
                log.error("Failed to clean up request: {}", request.getId(), e);
            }
        }
        
        log.info("Cleaned up {} old requests", cleanedCount);
        return cleanedCount;
    }
    
    @Override
    public Object getGenerationProgress(Long requestId, Long userId) {
        log.info("Getting generation progress for request: {}, user: {}", requestId, userId);
        
        // Get generation request
        AIGenerationRequest request = generationRequestRepository.findById(requestId)
                .orElseThrow(() -> new RuntimeException("Generation request not found"));
        
        // Verify user ownership
        if (!request.getUser().getId().equals(userId.intValue())) {
            throw new RuntimeException("Access denied to generation request");
        }
        
        // Get current progress from tracking
        Integer progress = requestProgress.get(requestId);
        if (progress == null) {
            progress = request.getProgress();
        }
        
        // Create progress response
        return Map.of(
            "requestId", requestId,
            "status", request.getStatus(),
            "progress", progress,
            "startedAt", request.getStartedAt(),
            "estimatedCompletion", request.getStartedAt() != null ? 
                request.getStartedAt().plusMinutes(5) : null,
            "currentStep", getCurrentStep(request.getStatus(), progress),
            "message", getProgressMessage(request.getStatus(), progress)
        );
    }
    
    private String getCurrentStep(AIGenerationRequest.GenerationStatus status, Integer progress) {
        switch (status) {
            case PENDING:
                return "Waiting to start";
            case PROCESSING:
                if (progress < 25) return "Initializing AI model";
                if (progress < 50) return "Processing prompt";
                if (progress < 75) return "Generating content";
                if (progress < 100) return "Finalizing output";
                return "Completing generation";
            case COMPLETED:
                return "Generation completed";
            case FAILED:
                return "Generation failed";
            case CANCELLED:
                return "Generation cancelled";
            default:
                return "Unknown status";
        }
    }
    
    private String getProgressMessage(AIGenerationRequest.GenerationStatus status, Integer progress) {
        switch (status) {
            case PENDING:
                return "Your request is queued and will start soon";
            case PROCESSING:
                if (progress < 25) return "Setting up AI model and preparing resources";
                if (progress < 50) return "Analyzing your prompt and generating ideas";
                if (progress < 75) return "Creating content based on your requirements";
                if (progress < 100) return "Finalizing and optimizing the output";
                return "Almost done! Finalizing your content";
            case COMPLETED:
                return "Content generation completed successfully";
            case FAILED:
                return "Content generation failed. You can retry or contact support";
            case CANCELLED:
                return "Content generation was cancelled";
            default:
                return "Unknown status";
        }
    }
    
    // Helper methods
    private boolean canCancelGeneration(AIGenerationRequest request) {
        return request.getStatus() == AIGenerationRequest.GenerationStatus.PENDING ||
               request.getStatus() == AIGenerationRequest.GenerationStatus.PROCESSING;
    }
    
    private boolean canRetryGeneration(AIGenerationRequest request) {
        return request.getStatus() == AIGenerationRequest.GenerationStatus.FAILED &&
               request.getRetryCount() < request.getMaxRetries();
    }
    
    private boolean canPreviewContent(Long userId) {
        // Simple rate limiting for previews
        return true; // TODO: Implement proper rate limiting
    }
    
    private void startProgressTracking(Long requestId) {
        requestProgress.put(requestId, 10);
        // TODO: Implement actual progress tracking with scheduled updates
    }
    
    private void stopProgressTracking(Long requestId) {
        requestProgress.remove(requestId);
    }
    
    private AIGeneratedContentDTO convertToContentDTO(AIGeneratedContent content) {
        return AIGeneratedContentDTO.builder()
                .id(content.getId())
                .generationRequestId(content.getGenerationRequest().getId())
                .userId(content.getUser().getId().longValue())
                .contentTitle(content.getContentTitle())
                .contentType(content.getContentType())
                .contentData(content.getContentData())
                .contentVersion(content.getContentVersion())
                .supabaseFilePath(content.getSupabaseFilePath())
                .fileSize(content.getFileSize())
                .isSaved(content.getIsSaved())
                .createdAt(content.getCreatedAt())
                .updatedAt(content.getUpdatedAt())
                .build();
    }
    
    private AIPromptTemplateDTO convertToTemplateDTO(AIPromptTemplate template) {
        return AIPromptTemplateDTO.builder()
                .id(template.getId())
                .templateName(template.getTemplateName())
                .templateDescription(template.getTemplateDescription())
                .promptText(template.getPromptText())
                .outputFormat(template.getOutputFormat())
                .isPublic(template.getIsPublic())
                .isActive(template.getIsActive())
                .usageCount(template.getUsageCount())
                .createdAt(template.getCreatedAt())
                .updatedAt(template.getUpdatedAt())
                .build();
    }
    
    // Inner classes for additional data
    @lombok.Data
    @lombok.Builder
    public static class PreviewResponse {
        private String contentTitle;
        private String contentType;
        private String contentData;
        private String previewFormat;
        private Boolean includeMetadata;
        private LocalDateTime generatedAt;
    }
    
    @lombok.Data
    @lombok.Builder
    public static class GenerationStats {
        private Long totalRequests;
        private Long completedRequests;
        private Long failedRequests;
        private Double successRate;
        private Long savedContent;
    }
}

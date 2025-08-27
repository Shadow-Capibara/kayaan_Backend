package se499.kayaanbackend.AI_Generate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se499.kayaanbackend.AI_Generate.dto.*;
import se499.kayaanbackend.AI_Generate.service.AIGenerationService;
import se499.kayaanbackend.security.user.User;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.concurrent.CompletableFuture;

/**
 * REST Controller for AI Generation feature
 * Covers all use cases: UC-19 to UC-27
 * 
 * Endpoints:
 * - POST /api/ai/generation/request - Create generation request
 * - POST /api/ai/generation/{requestId}/generate - Start content generation
 * - GET /api/ai/generation/{requestId}/status - Get generation status
 * - GET /api/ai/generation/requests - Get user's generation requests
 * - POST /api/ai/generation/{requestId}/cancel - Cancel generation
 * - POST /api/ai/generation/{requestId}/retry - Retry failed generation
 * - POST /api/ai/generation/content/save - Save generated content
 * - GET /api/ai/generation/content - Get user's saved content
 * - GET /api/ai/generation/content/{contentId}/download - Download content
 * - DELETE /api/ai/generation/content/{contentId} - Delete saved content
 * - POST /api/ai/generation/template - Create prompt template
 * - GET /api/ai/generation/template - Get user's templates
 * - GET /api/ai/generation/template/public - Get public templates
 * - PUT /api/ai/generation/template/{templateId} - Update template
 * - DELETE /api/ai/generation/template/{templateId} - Delete template
 * - POST /api/ai/generation/preview - Preview content
 * - GET /api/ai/generation/stats - Get user generation statistics
 * - POST /api/ai/generation/cleanup - Clean up old requests (admin only)
 */
@Slf4j
@RestController
@RequestMapping("/api/ai/generation")
@RequiredArgsConstructor
@Validated
@CrossOrigin(origins = "*")
public class AIGenerationController {

    private final AIGenerationService aiGenerationService;

    // ==================== GENERATION REQUESTS ====================

    /**
     * UC-19: Create AI Generation Request
     * POST /api/ai/generation/request
     */
    @PostMapping("/request")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<Long>> createGenerationRequest(
            @RequestPart("request") @Valid CreateGenerationRequestDTO requestDTO,
            @RequestPart(value = "file", required = false) MultipartFile file,
            @RequestAttribute("user") User user) {
        
        log.info("Creating generation request for user: {} with file: {}", 
                user.getId(), file != null ? file.getOriginalFilename() : "none");
        
        try {
            // Set file information if provided
            if (file != null && !file.isEmpty()) {
                requestDTO.setUploadedFile(file);
                requestDTO.setFileName(file.getOriginalFilename());
                requestDTO.setFileType(file.getContentType());
            }
            
            Long requestId = aiGenerationService.createGenerationRequest(requestDTO, user.getId().longValue());
            
            ApiResponseDTO<Long> response = ApiResponseDTO.<Long>builder()
                    .success(true)
                    .message("Generation request created successfully")
                    .data(requestId)
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Failed to create generation request", e);
            
            ApiResponseDTO<Long> response = ApiResponseDTO.<Long>builder()
                    .success(false)
                    .message("Failed to create generation request: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * UC-20: Start Content Generation
     * POST /api/ai/generation/{requestId}/generate
     */
    @PostMapping("/{requestId}/generate")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<String>> startContentGeneration(
            @PathVariable @NotNull Long requestId,
            @RequestAttribute("user") User user) {
        
        log.info("Starting content generation for request: {}, user: {}", requestId, user.getId());
        
        try {
            CompletableFuture<String> future = aiGenerationService.generateContent(requestId, user.getId().longValue());
            
            // For now, we'll return immediately with a message
            // In a real implementation, you might want to implement WebSocket for real-time updates
            ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                    .success(true)
                    .message("Content generation started successfully. Check status for progress updates.")
                    .data("Generation in progress")
                    .build();
            
            return ResponseEntity.accepted().body(response);
            
        } catch (Exception e) {
            log.error("Failed to start content generation", e);
            
            ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                    .success(false)
                    .message("Failed to start content generation: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * UC-21: Get Generation Status
     * GET /api/ai/generation/{requestId}/status
     */
    @GetMapping("/{requestId}/status")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<GenerationStatusDTO>> getGenerationStatus(
            @PathVariable @NotNull Long requestId,
            @RequestAttribute("user") User user) {
        
        log.info("Getting generation status for request: {}, user: {}", requestId, user.getId());
        
        try {
            GenerationStatusDTO status = aiGenerationService.getGenerationStatus(requestId, user.getId().longValue());
            
            ApiResponseDTO<GenerationStatusDTO> response = ApiResponseDTO.<GenerationStatusDTO>builder()
                    .success(true)
                    .message("Generation status retrieved successfully")
                    .data(status)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get generation status", e);
            
            ApiResponseDTO<GenerationStatusDTO> response = ApiResponseDTO.<GenerationStatusDTO>builder()
                    .success(false)
                    .message("Failed to get generation status: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }
    
    /**
     * UC-21.1: Get Real-time Generation Progress
     * GET /api/ai/generation/{requestId}/progress
     */
    @GetMapping("/{requestId}/progress")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<Object>> getGenerationProgress(
            @PathVariable @NotNull Long requestId,
            @RequestAttribute("user") User user) {
        
        log.info("Getting generation progress for request: {}, user: {}", requestId, user.getId());
        
        try {
            // This endpoint is for initial progress check
            // Real-time updates are sent via WebSocket
            Object progress = aiGenerationService.getGenerationProgress(requestId, user.getId().longValue());
            
            ApiResponseDTO<Object> response = ApiResponseDTO.<Object>builder()
                    .success(true)
                    .message("Generation progress retrieved successfully")
                    .data(progress)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get generation progress", e);
            
            ApiResponseDTO<Object> response = ApiResponseDTO.<Object>builder()
                    .success(false)
                    .message("Failed to get generation progress: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * UC-22: Get User's Generation Requests
     * GET /api/ai/generation/requests
     */
    @GetMapping("/requests")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<Page<GenerationStatusDTO>>> getUserGenerationRequests(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Min(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestAttribute("user") User user) {
        
        log.info("Getting generation requests for user: {}, page: {}, size: {}", user.getId(), page, size);
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<GenerationStatusDTO> requests = aiGenerationService.getUserGenerationRequests(user.getId().longValue(), pageable);
            
            ApiResponseDTO<Page<GenerationStatusDTO>> response = ApiResponseDTO.<Page<GenerationStatusDTO>>builder()
                    .success(true)
                    .message("Generation requests retrieved successfully")
                    .data(requests)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get generation requests", e);
            
            ApiResponseDTO<Page<GenerationStatusDTO>> response = ApiResponseDTO.<Page<GenerationStatusDTO>>builder()
                    .success(false)
                    .message("Failed to get generation requests: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * UC-23: Cancel Generation
     * POST /api/ai/generation/{requestId}/cancel
     */
    @PostMapping("/{requestId}/cancel")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<Boolean>> cancelGeneration(
            @PathVariable @NotNull Long requestId,
            @RequestAttribute("user") User user) {
        
        log.info("Cancelling generation request: {}, user: {}", requestId, user.getId());
        
        try {
            boolean cancelled = aiGenerationService.cancelGeneration(requestId, user.getId().longValue());
            
            ApiResponseDTO<Boolean> response = ApiResponseDTO.<Boolean>builder()
                    .success(true)
                    .message("Generation request cancelled successfully")
                    .data(cancelled)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to cancel generation", e);
            
            ApiResponseDTO<Boolean> response = ApiResponseDTO.<Boolean>builder()
                    .success(false)
                    .message("Failed to cancel generation: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * UC-24: Retry Failed Generation
     * POST /api/ai/generation/{requestId}/retry
     */
    @PostMapping("/{requestId}/retry")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<Long>> retryGeneration(
            @PathVariable @NotNull Long requestId,
            @RequestAttribute("user") User user) {
        
        log.info("Retrying generation request: {}, user: {}", requestId, user.getId());
        
        try {
            Long newRequestId = aiGenerationService.retryGeneration(requestId, user.getId().longValue());
            
            ApiResponseDTO<Long> response = ApiResponseDTO.<Long>builder()
                    .success(true)
                    .message("Generation request retry created successfully")
                    .data(newRequestId)
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Failed to retry generation", e);
            
            ApiResponseDTO<Long> response = ApiResponseDTO.<Long>builder()
                    .success(false)
                    .message("Failed to retry generation: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== CONTENT MANAGEMENT ====================

    /**
     * UC-25: Save Generated Content
     * POST /api/ai/generation/content/save
     */
    @PostMapping("/content/save")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<Long>> saveGeneratedContent(
            @Valid @RequestBody SaveContentDTO contentDTO,
            @RequestAttribute("user") User user) {
        
        log.info("Saving generated content for user: {}", user.getId());
        
        try {
            Long contentId = aiGenerationService.saveGeneratedContent(contentDTO, user.getId().longValue());
            
            ApiResponseDTO<Long> response = ApiResponseDTO.<Long>builder()
                    .success(true)
                    .message("Generated content saved successfully")
                    .data(contentId)
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Failed to save generated content", e);
            
            ApiResponseDTO<Long> response = ApiResponseDTO.<Long>builder()
                    .success(false)
                    .message("Failed to save generated content: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * UC-26: Get User's Saved Content
     * GET /api/ai/generation/content
     */
    @GetMapping("/content")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<Page<AIGeneratedContentDTO>>> getUserSavedContent(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Min(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestAttribute("user") User user) {
        
        log.info("Getting saved content for user: {}, page: {}, size: {}", user.getId(), page, size);
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<AIGeneratedContentDTO> content = aiGenerationService.getUserSavedContent(user.getId().longValue(), pageable);
            
            ApiResponseDTO<Page<AIGeneratedContentDTO>> response = ApiResponseDTO.<Page<AIGeneratedContentDTO>>builder()
                    .success(true)
                    .message("Saved content retrieved successfully")
                    .data(content)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get saved content", e);
            
            ApiResponseDTO<Page<AIGeneratedContentDTO>> response = ApiResponseDTO.<Page<AIGeneratedContentDTO>>builder()
                    .success(false)
                    .message("Failed to get saved content: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Download Content
     * GET /api/ai/generation/content/{contentId}/download
     */
    @GetMapping("/content/{contentId}/download")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<String>> downloadContent(
            @PathVariable @NotNull Long contentId,
            @RequestAttribute("user") User user) {
        
        log.info("Downloading content: {}, user: {}", contentId, user.getId());
        
        try {
            String content = aiGenerationService.downloadContent(contentId, user.getId().longValue());
            
            ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                    .success(true)
                    .message("Content downloaded successfully")
                    .data(content)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to download content", e);
            
            ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                    .success(false)
                    .message("Failed to download content: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Delete Saved Content
     * DELETE /api/ai/generation/content/{contentId}
     */
    @DeleteMapping("/content/{contentId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<Boolean>> deleteSavedContent(
            @PathVariable @NotNull Long contentId,
            @RequestAttribute("user") User user) {
        
        log.info("Deleting saved content: {}, user: {}", contentId, user.getId());
        
        try {
            boolean deleted = aiGenerationService.deleteSavedContent(contentId, user.getId().longValue());
            
            ApiResponseDTO<Boolean> response = ApiResponseDTO.<Boolean>builder()
                    .success(true)
                    .message("Content deleted successfully")
                    .data(deleted)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to delete content", e);
            
            ApiResponseDTO<Boolean> response = ApiResponseDTO.<Boolean>builder()
                    .success(false)
                    .message("Failed to delete content: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== TEMPLATE MANAGEMENT ====================

    /**
     * Create Prompt Template
     * POST /api/ai/generation/template
     */
    @PostMapping("/template")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<Long>> createPromptTemplate(
            @Valid @RequestBody AIPromptTemplateDTO templateDTO,
            @RequestAttribute("user") User user) {
        
        log.info("Creating prompt template for user: {}", user.getId());
        
        try {
            Long templateId = aiGenerationService.createPromptTemplate(templateDTO, user.getId().longValue());
            
            ApiResponseDTO<Long> response = ApiResponseDTO.<Long>builder()
                    .success(true)
                    .message("Prompt template created successfully")
                    .data(templateId)
                    .build();
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            log.error("Failed to create prompt template", e);
            
            ApiResponseDTO<Long> response = ApiResponseDTO.<Long>builder()
                    .success(false)
                    .message("Failed to create prompt template: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Get User's Templates
     * GET /api/ai/generation/template
     */
    @GetMapping("/template")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<Page<AIPromptTemplateDTO>>> getUserTemplates(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Min(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir,
            @RequestAttribute("user") User user) {
        
        log.info("Getting templates for user: {}, page: {}, size: {}", user.getId(), page, size);
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<AIPromptTemplateDTO> templates = aiGenerationService.getUserTemplates(user.getId().longValue(), pageable);
            
            ApiResponseDTO<Page<AIPromptTemplateDTO>> response = ApiResponseDTO.<Page<AIPromptTemplateDTO>>builder()
                    .success(true)
                    .message("Templates retrieved successfully")
                    .data(templates)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get templates", e);
            
            ApiResponseDTO<Page<AIPromptTemplateDTO>> response = ApiResponseDTO.<Page<AIPromptTemplateDTO>>builder()
                    .success(false)
                    .message("Failed to get templates: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Get Public Templates
     * GET /api/ai/generation/template/public
     */
    @GetMapping("/template/public")
    public ResponseEntity<ApiResponseDTO<Page<AIPromptTemplateDTO>>> getPublicTemplates(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) @Min(100) int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {
        
        log.info("Getting public templates, page: {}, size: {}", page, size);
        
        try {
            Sort.Direction direction = sortDir.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
            Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
            
            Page<AIPromptTemplateDTO> templates = aiGenerationService.getPublicTemplates(pageable);
            
            ApiResponseDTO<Page<AIPromptTemplateDTO>> response = ApiResponseDTO.<Page<AIPromptTemplateDTO>>builder()
                    .success(true)
                    .message("Public templates retrieved successfully")
                    .data(templates)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get public templates", e);
            
            ApiResponseDTO<Page<AIPromptTemplateDTO>> response = ApiResponseDTO.<Page<AIPromptTemplateDTO>>builder()
                    .success(false)
                    .message("Failed to get public templates: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Update Prompt Template
     * PUT /api/ai/generation/template/{templateId}
     */
    @PutMapping("/template/{templateId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<AIPromptTemplateDTO>> updatePromptTemplate(
            @PathVariable @NotNull Long templateId,
            @Valid @RequestBody AIPromptTemplateDTO templateDTO,
            @RequestAttribute("user") User user) {
        
        log.info("Updating prompt template: {}, user: {}", templateId, user.getId());
        
        try {
            AIPromptTemplateDTO updatedTemplate = aiGenerationService.updatePromptTemplate(templateId, templateDTO, user.getId().longValue());
            
            ApiResponseDTO<AIPromptTemplateDTO> response = ApiResponseDTO.<AIPromptTemplateDTO>builder()
                    .success(true)
                    .message("Prompt template updated successfully")
                    .data(updatedTemplate)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to update prompt template", e);
            
            ApiResponseDTO<AIPromptTemplateDTO> response = ApiResponseDTO.<AIPromptTemplateDTO>builder()
                    .success(false)
                    .message("Failed to update prompt template: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Delete Prompt Template
     * DELETE /api/ai/generation/template/{templateId}
     */
    @DeleteMapping("/template/{templateId}")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<Boolean>> deletePromptTemplate(
            @PathVariable @NotNull Long templateId,
            @RequestAttribute("user") User user) {
        
        log.info("Deleting prompt template: {}, user: {}", templateId, user.getId());
        
        try {
            boolean deleted = aiGenerationService.deletePromptTemplate(templateId, user.getId().longValue());
            
            ApiResponseDTO<Boolean> response = ApiResponseDTO.<Boolean>builder()
                    .success(true)
                    .message("Prompt template deleted successfully")
                    .data(deleted)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to delete prompt template", e);
            
            ApiResponseDTO<Boolean> response = ApiResponseDTO.<Boolean>builder()
                    .success(false)
                    .message("Failed to delete prompt template: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== ADDITIONAL FEATURES ====================

    /**
     * Preview Content
     * POST /api/ai/generation/preview
     */
    @PostMapping("/preview")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<Object>> previewContent(
            @Valid @RequestBody PreviewRequestDTO previewDTO,
            @RequestAttribute("user") User user) {
        
        log.info("Previewing content for user: {}", user.getId());
        
        try {
            Object preview = aiGenerationService.previewContent(previewDTO, user.getId().longValue());
            
            ApiResponseDTO<Object> response = ApiResponseDTO.<Object>builder()
                    .success(true)
                    .message("Content preview generated successfully")
                    .data(preview)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to preview content", e);
            
            ApiResponseDTO<Object> response = ApiResponseDTO.<Object>builder()
                    .success(false)
                    .message("Failed to preview content: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Get User Generation Statistics
     * GET /api/ai/generation/stats
     */
    @GetMapping("/stats")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<ApiResponseDTO<Object>> getUserGenerationStats(
            @RequestAttribute("user") User user) {
        
        log.info("Getting generation stats for user: {}", user.getId());
        
        try {
            Object stats = aiGenerationService.getUserGenerationStats(user.getId().longValue());
            
            ApiResponseDTO<Object> response = ApiResponseDTO.<Object>builder()
                    .success(true)
                    .message("Generation statistics retrieved successfully")
                    .data(stats)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to get generation stats", e);
            
            ApiResponseDTO<Object> response = ApiResponseDTO.<Object>builder()
                    .success(false)
                    .message("Failed to get generation statistics: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    /**
     * Cleanup Old Requests (Admin Only)
     * POST /api/ai/generation/cleanup
     */
    @PostMapping("/cleanup")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ApiResponseDTO<Integer>> cleanupOldRequests(
            @RequestParam(defaultValue = "30") @Min(1) int daysOld) {
        
        log.info("Cleaning up requests older than {} days", daysOld);
        
        try {
            int cleanedCount = aiGenerationService.cleanupOldRequests(daysOld);
            
            ApiResponseDTO<Integer> response = ApiResponseDTO.<Integer>builder()
                    .success(true)
                    .message("Cleanup completed successfully")
                    .data(cleanedCount)
                    .build();
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Failed to cleanup old requests", e);
            
            ApiResponseDTO<Integer> response = ApiResponseDTO.<Integer>builder()
                    .success(false)
                    .message("Failed to cleanup old requests: " + e.getMessage())
                    .build();
            
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }
    }

    // ==================== HEALTH CHECK ====================

    /**
     * Health Check Endpoint
     * GET /api/ai/generation/health
     */
    @GetMapping("/health")
    public ResponseEntity<ApiResponseDTO<String>> healthCheck() {
        ApiResponseDTO<String> response = ApiResponseDTO.<String>builder()
                .success(true)
                .message("AI Generation Service is running")
                .data("OK")
                .build();
        
        return ResponseEntity.ok(response);
    }
}

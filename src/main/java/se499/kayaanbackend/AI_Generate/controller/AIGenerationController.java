package se499.kayaanbackend.AI_Generate.controller;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.AI_Generate.dto.AIGeneratedContentDTO;
import se499.kayaanbackend.AI_Generate.dto.CreateGenerationRequestDTO;
import se499.kayaanbackend.AI_Generate.dto.GenerationStatusDTO;
import se499.kayaanbackend.AI_Generate.dto.PreviewRequestDTO;
import se499.kayaanbackend.AI_Generate.dto.SaveContentDTO;
import se499.kayaanbackend.AI_Generate.exception.InvalidContentTypeException;
import se499.kayaanbackend.AI_Generate.service.AIGenerationService;
import se499.kayaanbackend.AI_Generate.service.ContentTypeValidationService;
import se499.kayaanbackend.security.user.User;

/**
 * Controller for AI Generation feature
 * Handles all AI content generation endpoints
 */
@RestController
@RequestMapping("/api/ai/generation")
@RequiredArgsConstructor
@Slf4j
public class AIGenerationController {

    private final AIGenerationService aiGenerationService;
    private final ContentTypeValidationService contentTypeValidationService;

    /**
     * Create a new generation request
     * POST /api/ai/generation/request
     */
    @PostMapping("/request")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createGenerationRequest(
            @AuthenticationPrincipal User user,
            @RequestParam("request") String requestJson,
            @RequestParam(value = "file", required = false) MultipartFile file
    ) {
        try {
            log.info("Creating generation request for user: {}", user.getId());
            log.info("Raw request JSON received: {}", requestJson);
            
            // Parse request JSON and validate content type
            CreateGenerationRequestDTO dto = parseCreateRequest(requestJson);
            log.info("Parsed DTO: promptText={}, outputFormat={}", dto.getPromptText(), dto.getOutputFormat());
            
            // Validate content type
            if (!contentTypeValidationService.isValidContentType(dto.getOutputFormat())) {
                throw new InvalidContentTypeException(dto.getOutputFormat(), 
                    new String[]{"flashcard", "quiz", "note"});
            }
            
            // Add file to DTO if provided
            if (file != null && !file.isEmpty()) {
                dto.setUploadedFile(file);
                dto.setFileName(file.getOriginalFilename());
                dto.setFileType(getFileExtension(file.getOriginalFilename()));
            }
            
            Long requestId = aiGenerationService.createGenerationRequest(dto, user.getId().longValue());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Generation request created successfully",
                "data", requestId
            ));
            
        } catch (InvalidContentTypeException e) {
            log.warn("Invalid content type: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage(),
                "error", "INVALID_CONTENT_TYPE"
            ));
        } catch (Exception e) {
            log.error("Error creating generation request", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to create generation request: " + e.getMessage(),
                "error", "GENERATION_REQUEST_FAILED"
            ));
        }
    }

    /**
     * Start content generation for a request
     * POST /api/ai/generation/{requestId}/generate
     */
    @PostMapping("/{requestId}/generate")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> startGeneration(
            @AuthenticationPrincipal User user,
            @PathVariable Long requestId
    ) {
        try {
            log.info("Starting generation for request: {} by user: {}", requestId, user.getId());
            
            CompletableFuture<String> future = aiGenerationService.generateContent(requestId, user.getId().longValue());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Content generation started successfully. Check status for progress updates.",
                "data", "Generation in progress"
            ));
            
        } catch (Exception e) {
            log.error("Error starting generation for request: {}", requestId, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to start generation: " + e.getMessage(),
                "error", "GENERATION_START_FAILED"
            ));
        }
    }

    /**
     * Get generation status
     * GET /api/ai/generation/{requestId}/status
     */
    @GetMapping("/{requestId}/status")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getGenerationStatus(
            @AuthenticationPrincipal User user,
            @PathVariable Long requestId
    ) {
        try {
            log.info("Getting status for generation request: {} by user: {}", requestId, user.getId());
            
            GenerationStatusDTO status = aiGenerationService.getGenerationStatus(requestId, user.getId().longValue());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Generation status retrieved successfully",
                "data", status
            ));
            
        } catch (Exception e) {
            log.error("Error getting generation status for request: {}", requestId, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to get generation status: " + e.getMessage(),
                "error", "STATUS_RETRIEVAL_FAILED"
            ));
        }
    }

    /**
     * Get user's generation requests
     * GET /api/ai/generation/requests
     */
    @GetMapping("/requests")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserGenerationRequests(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        try {
            log.info("Getting generation requests for user: {} (page: {}, size: {})", user.getId(), page, size);
            
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir.toUpperCase()), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<GenerationStatusDTO> requests = aiGenerationService.getUserGenerationRequests(user.getId().longValue(), pageable);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Generation requests retrieved successfully",
                "data", requests
            ));
            
        } catch (Exception e) {
            log.error("Error getting generation requests for user: {}", user.getId(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to get generation requests: " + e.getMessage(),
                "error", "REQUESTS_RETRIEVAL_FAILED"
            ));
        }
    }

    /**
     * Save generated content
     * POST /api/ai/generation/content/save
     */
    @PostMapping("/content/save")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> saveGeneratedContent(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody SaveContentDTO dto
    ) {
        try {
            log.info("Saving generated content for user: {}", user.getId());
            
            // Validate content type
            if (!contentTypeValidationService.isValidContentType(dto.getContentType())) {
                throw new InvalidContentTypeException(dto.getContentType(), 
                    new String[]{"flashcard", "quiz", "note"});
            }
            
            Long contentId = aiGenerationService.saveGeneratedContent(dto, user.getId().longValue());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Content saved successfully",
                "data", contentId
            ));
            
        } catch (InvalidContentTypeException e) {
            log.warn("Invalid content type: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage(),
                "error", "INVALID_CONTENT_TYPE"
            ));
        } catch (Exception e) {
            log.error("Error saving generated content", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to save content: " + e.getMessage(),
                "error", "CONTENT_SAVE_FAILED"
            ));
        }
    }

    /**
     * Get user's saved content
     * GET /api/ai/generation/content
     */
    @GetMapping("/content")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getUserSavedContent(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        try {
            log.info("Getting saved content for user: {} (page: {}, size: {})", user.getId(), page, size);
            
            Sort sort = Sort.by(Sort.Direction.fromString(sortDir.toUpperCase()), sortBy);
            Pageable pageable = PageRequest.of(page, size, sort);
            
            Page<AIGeneratedContentDTO> content = aiGenerationService.getUserSavedContent(user.getId().longValue(), pageable);
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Saved content retrieved successfully",
                "data", content
            ));
            
        } catch (Exception e) {
            log.error("Error getting saved content for user: {}", user.getId(), e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to get saved content: " + e.getMessage(),
                "error", "CONTENT_RETRIEVAL_FAILED"
            ));
        }
    }

    /**
     * Cancel generation request
     * POST /api/ai/generation/{requestId}/cancel
     */
    @PostMapping("/{requestId}/cancel")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> cancelGeneration(
            @AuthenticationPrincipal User user,
            @PathVariable Long requestId
    ) {
        try {
            log.info("Cancelling generation request: {} by user: {}", requestId, user.getId());
            
            boolean cancelled = aiGenerationService.cancelGeneration(requestId, user.getId().longValue());
            
            if (cancelled) {
                return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Generation request cancelled successfully",
                    "data", "Cancelled"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to cancel generation request",
                    "error", "CANCELLATION_FAILED"
                ));
            }
            
        } catch (Exception e) {
            log.error("Error cancelling generation request: {}", requestId, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to cancel generation: " + e.getMessage(),
                "error", "CANCELLATION_FAILED"
            ));
        }
    }

    /**
     * Retry failed generation request
     * POST /api/ai/generation/{requestId}/retry
     */
    @PostMapping("/{requestId}/retry")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> retryGeneration(
            @AuthenticationPrincipal User user,
            @PathVariable Long requestId
    ) {
        try {
            log.info("Retrying generation request: {} by user: {}", requestId, user.getId());
            
            Long newRequestId = aiGenerationService.retryGeneration(requestId, user.getId().longValue());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Generation request retried successfully",
                "data", newRequestId
            ));
            
        } catch (Exception e) {
            log.error("Error retrying generation request: {}", requestId, e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to retry generation: " + e.getMessage(),
                "error", "RETRY_FAILED"
            ));
        }
    }

    /**
     * Preview generated content
     * POST /api/ai/generation/preview
     */
    @PostMapping("/preview")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> previewContent(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody PreviewRequestDTO dto
    ) {
        try {
            log.info("Previewing content for user: {}", user.getId());
            
            // Validate content type
            if (!contentTypeValidationService.isValidContentType(dto.getContentType())) {
                throw new InvalidContentTypeException(dto.getContentType(), 
                    new String[]{"flashcard", "quiz", "note"});
            }
            
            Object preview = aiGenerationService.previewContent(dto, user.getId().longValue());
            
            return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Content preview generated successfully",
                "data", preview
            ));
            
        } catch (InvalidContentTypeException e) {
            log.warn("Invalid content type: {}", e.getMessage());
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", e.getMessage(),
                "error", "INVALID_CONTENT_TYPE"
            ));
        } catch (Exception e) {
            log.error("Error previewing content", e);
            return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Failed to preview content: " + e.getMessage(),
                "error", "PREVIEW_FAILED"
            ));
        }
    }

    // Helper methods
    private CreateGenerationRequestDTO parseCreateRequest(String requestJson) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(requestJson, CreateGenerationRequestDTO.class);
        } catch (Exception e) {
            log.error("Failed to parse request JSON: {}", requestJson, e);
            throw new RuntimeException("Invalid JSON format", e);
        }
    }

    private String getFileExtension(String fileName) {
        if (fileName == null || fileName.isEmpty()) {
            return "";
        }
        int lastDotIndex = fileName.lastIndexOf('.');
        if (lastDotIndex > 0) {
            return fileName.substring(lastDotIndex + 1).toLowerCase();
        }
        return "";
    }
}

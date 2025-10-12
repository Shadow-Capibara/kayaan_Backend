package se499.kayaanbackend.Manual_Generate.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.AI_Generate.entity.ContentType;
import se499.kayaanbackend.Manual_Generate.dto.CreateManualContentDTO;
import se499.kayaanbackend.Manual_Generate.dto.ManualGeneratedContentDTO;
import se499.kayaanbackend.Manual_Generate.exception.ContentValidationException;
import se499.kayaanbackend.Manual_Generate.exception.ManualGenerationException;
import se499.kayaanbackend.Manual_Generate.service.ManualGeneratedContentService;
import se499.kayaanbackend.security.user.User;

/**
 * Unified Controller for Manual Content Generation (JSON format)
 * Similar to AI Generation - one endpoint for all content types
 */
@RestController
@RequestMapping("/api/content/manual")
@RequiredArgsConstructor
@Slf4j
public class ManualGeneratedContentController {
    
    private final ManualGeneratedContentService manualGeneratedContentService;
    
    /**
     * Create manual content (Flashcard, Quiz, Note) in JSON format
     * POST /api/content/manual
     */
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createManualContent(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CreateManualContentDTO request) {
        try {
            log.info("=== Creating manual content ===");
            log.info("User: {}, Username: {}", user.getId(), user.getUsername());
            log.info("Content type: {}", request.getContentType());
            log.info("Content title: {}", request.getContentTitle());
            log.info("Content data: {}", request.getContentData());
            log.info("Subject: {}, Difficulty: {}, Tags: {}", 
                request.getSubject(), request.getDifficulty(), request.getTags());
            
            // Validate request
            if (request.getContentTitle() == null || request.getContentTitle().trim().isEmpty()) {
                log.error("Content title is null or empty");
                return ResponseEntity.badRequest().build();
            }
            
            if (request.getContentType() == null || request.getContentType().trim().isEmpty()) {
                log.error("Content type is null or empty");
                return ResponseEntity.badRequest().build();
            }
            
            if (request.getContentData() == null || request.getContentData().trim().isEmpty()) {
                log.error("Content data is null or empty");
                return ResponseEntity.badRequest().build();
            }
            
            log.info("Basic validation passed, calling service...");
            ManualGeneratedContentDTO createdContent = manualGeneratedContentService.createContent(request, user.getUsername());
            
            log.info("Successfully created manual content: {}", createdContent.getId());
            return ResponseEntity.ok(createdContent);
            
        } catch (Exception e) {
            log.error("Failed to create manual content for user: {}", user.getUsername(), e);
            
            // Return detailed error response
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "ContentCreationFailed");
            errorResponse.put("message", "Failed to create manual content: " + e.getMessage());
            
            if (e instanceof ContentValidationException) {
                errorResponse.put("errorType", "ValidationError");
                errorResponse.put("details", "Content validation failed: " + e.getMessage());
                return ResponseEntity.badRequest().body(errorResponse);
            } else if (e instanceof ManualGenerationException) {
                errorResponse.put("errorType", "ManualGenerationError");
                errorResponse.put("details", "Manual generation failed: " + e.getMessage());
                return ResponseEntity.badRequest().body(errorResponse);
            } else {
                errorResponse.put("errorType", "UnknownError");
                errorResponse.put("details", "An unexpected error occurred: " + e.getMessage());
                return ResponseEntity.internalServerError().body(errorResponse);
            }
        }
    }
    
    /**
     * Test endpoint for validation (no auth required)
     * POST /api/content/manual/test
     */
    @PostMapping("/test")
    public ResponseEntity<Map<String, Object>> testValidation(
            @RequestBody @Valid CreateManualContentDTO request) {
        try {
            log.info("Testing validation for content type: {}", request.getContentType());
            log.debug("Request details: {}", request);
            
            // Test business logic validation
            log.info("Calling validateJsonContent with contentType: {} and contentData: {}", request.getContentType(), request.getContentData());
            boolean isValidContent = manualGeneratedContentService.validateJsonContent(request.getContentType(), request.getContentData());
            log.info("Business validation result: {}", isValidContent);
            
            if (!isValidContent) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Business validation failed");
                response.put("error", "InvalidContentStructure");
                response.put("details", "Content structure does not match expected format for type: " + request.getContentType());
                return ResponseEntity.badRequest().body(response);
            }
            
            // Test content type validation
            try {
                se499.kayaanbackend.AI_Generate.entity.ContentType.fromString(request.getContentType());
            } catch (IllegalArgumentException e) {
                Map<String, Object> response = new HashMap<>();
                response.put("success", false);
                response.put("message", "Invalid content type");
                response.put("error", "InvalidContentType");
                response.put("details", e.getMessage());
                return ResponseEntity.badRequest().body(response);
            }
            
            // All validations passed
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "All validations passed");
            response.put("contentType", request.getContentType());
            response.put("contentTitle", request.getContentTitle());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Validation test failed", e);
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("message", "Validation failed: " + e.getMessage());
            response.put("error", "ValidationError");
            return ResponseEntity.badRequest().body(response);
        }
    }

    /**
     * Get manual content by type
     * GET /api/content/manual?type=flashcard|quiz|note
     */
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<ManualGeneratedContentDTO>> getManualContent(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false, defaultValue = "all") String type) {
        try {
            log.info("Getting manual content for user: {}, type: {}", user.getUsername(), type);
            
            List<ManualGeneratedContentDTO> content;
            if ("all".equals(type)) {
                content = manualGeneratedContentService.getAllContentForUser(user.getUsername());
            } else {
                ContentType contentType = ContentType.fromString(type);
                content = manualGeneratedContentService.getContentByTypeForUser(user.getUsername(), contentType);
            }
            
            // Debug logging for note content
            if ("note".equals(type) || "all".equals(type)) {
                content.stream()
                    .filter(c -> "note".equals(c.getContentType()))
                    .forEach(note -> {
                        log.info("Debug - Note '{}' (ID: {}) content data: {}", 
                                note.getContentTitle(), note.getId(), note.getContentData());
                    });
            }
            
            log.info("Successfully retrieved {} manual content items for user: {}", 
                    content.size(), user.getUsername());
            return ResponseEntity.ok(content);
            
        } catch (Exception e) {
            log.error("Failed to get manual content for user: {}", user.getUsername(), e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * Delete manual content by ID
     * DELETE /api/content/manual/{id}
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> deleteManualContent(
            @AuthenticationPrincipal User user,
            @PathVariable Long id) {
        try {
            log.info("=== Deleting manual content ===");
            log.info("User: {}, Username: {}", user.getId(), user.getUsername());
            log.info("Content ID: {}", id);
            
            // Validate input
            if (id == null || id <= 0) {
                log.error("Invalid content ID: {} (type: {})", id, id != null ? id.getClass().getSimpleName() : "null");
                
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("error", "InvalidContentId");
                errorResponse.put("message", "Content ID must be a positive number. Received: " + id);
                errorResponse.put("contentId", id);
                
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
            // Call service to delete content
            manualGeneratedContentService.deleteContent(id, user.getUsername());
            
            log.info("Successfully deleted manual content: {} for user: {}", id, user.getUsername());
            
            // Return success response
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Content deleted successfully");
            response.put("contentId", id);
            
            return ResponseEntity.ok(response);
            
        } catch (ManualGenerationException e) {
            log.error("Failed to delete manual content {} for user: {} - {}", 
                    id, user.getUsername(), e.getMessage());
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "ContentDeletionFailed");
            errorResponse.put("message", "Failed to delete content: " + e.getMessage());
            errorResponse.put("contentId", id);
            
            if (e.getMessage().contains("not found") || e.getMessage().contains("access denied")) {
                errorResponse.put("errorType", "NotFoundError");
                return ResponseEntity.notFound().build();
            } else {
                errorResponse.put("errorType", "ManualGenerationError");
                return ResponseEntity.badRequest().body(errorResponse);
            }
            
        } catch (Exception e) {
            log.error("Unexpected error deleting manual content {} for user: {}", 
                    id, user.getUsername(), e);
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", "UnknownError");
            errorResponse.put("message", "An unexpected error occurred: " + e.getMessage());
            errorResponse.put("contentId", id);
            
            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }
}

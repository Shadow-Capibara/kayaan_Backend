package se499.kayaanbackend.AI_Generate.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.AI_Generate.entity.AIDraft;
import se499.kayaanbackend.AI_Generate.entity.AIGeneratedContent;
import se499.kayaanbackend.AI_Generate.entity.AIGenerationRequest;
import se499.kayaanbackend.AI_Generate.entity.AIJob;
import se499.kayaanbackend.AI_Generate.repository.AIGeneratedContentRepository;
import se499.kayaanbackend.AI_Generate.repository.AIGenerationRequestRepository;
import se499.kayaanbackend.AI_Generate.service.AIJobService;
import se499.kayaanbackend.AI_Generate.service.OpenAIService;
import se499.kayaanbackend.security.user.User;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AIJobController {
    
    private final AIJobService aiJobService;
    private final OpenAIService openAIService;
    private final AIGenerationRequestRepository generationRequestRepository;
    private final AIGeneratedContentRepository generatedContentRepository;
    
    /**
     * ขอ Upload URL สำหรับ AI Job
     */
    @PostMapping("/jobs/{id}/request-upload")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> requestUploadUrl(
            @PathVariable String id,
            @RequestBody AIJobUploadRequest request
    ) {
        try {
            var signedUrl = aiJobService.requestOutputUploadUrl(id, request.fileName(), request.contentType());
            
            return ResponseEntity.ok(Map.of(
                "signedUrl", signedUrl.url(),
                "path", signedUrl.path(),
                "expiresIn", signedUrl.expiresInSeconds()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * สร้าง AI Job ใหม่
     */
    @PostMapping("/jobs")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createJob(
            @AuthenticationPrincipal User user,
            @RequestBody CreateJobRequest request
    ) {
        try {
            AIJob job = aiJobService.createJob(user, request.jobType(), request.inputData());
            return ResponseEntity.ok(job);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * ประมวลผล AI Job
     */
    @PostMapping("/jobs/{id}/process")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> processJob(@PathVariable Long id) {
        try {
            AIJob job = aiJobService.processJob(id);
            return ResponseEntity.ok(job);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * สร้าง AI Draft
     */
    @PostMapping("/drafts")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> createDraft(
            @AuthenticationPrincipal User user,
            @RequestBody CreateDraftRequest request
    ) {
        try {
            AIDraft draft = aiJobService.createDraft(user, request.draftType(), request.title(), request.content());
            return ResponseEntity.ok(draft);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * บันทึก AI Draft
     */
    @PutMapping("/drafts/{id}/save")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> saveDraft(@PathVariable Long id) {
        try {
            AIDraft draft = aiJobService.saveDraft(id);
            return ResponseEntity.ok(draft);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * ทดสอบ OpenAI Configuration (Public endpoint for testing)
     */
    @GetMapping("/config/test")
    public ResponseEntity<?> testOpenAIConfig() {
        try {
            boolean isValid = openAIService.isConfigurationValid();
            boolean isConnected = openAIService.testConnection();
            
            return ResponseEntity.ok(Map.of(
                "configurationValid", isValid,
                "connectionTest", isConnected,
                "status", isValid && isConnected ? "READY" : "NOT_READY"
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Debug Generation Request (Public endpoint for debugging)
     */
    @GetMapping("/debug/request/{id}")
    public ResponseEntity<Map<String, Object>> debugGenerationRequest(@PathVariable Long id) {
        try {
            Optional<AIGenerationRequest> request = generationRequestRepository.findById(id);
            Map<String, Object> response = new HashMap<>();
            
            if (request.isPresent()) {
                AIGenerationRequest req = request.get();
                response.put("id", req.getId());
                response.put("status", req.getStatus());
                response.put("progress", req.getProgress());
                response.put("promptText", req.getPromptText());
                response.put("outputFormat", req.getOutputFormat());
                response.put("errorMessage", req.getErrorMessage());
                response.put("createdAt", req.getCreatedAt());
                response.put("startedAt", req.getStartedAt());
                response.put("completedAt", req.getCompletedAt());
                response.put("retryCount", req.getRetryCount());
                response.put("maxRetries", req.getMaxRetries());
                
                // Check for generated content
                Optional<AIGeneratedContent> content = generatedContentRepository
                    .findLatestVersionByRequestId(id);
                response.put("hasGeneratedContent", content.isPresent());
                if (content.isPresent()) {
                    response.put("contentData", content.get().getContentData());
                    response.put("contentTitle", content.get().getContentTitle());
                }
                
                response.put("success", true);
            } else {
                response.put("success", false);
                response.put("message", "Request not found");
            }
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    /**
     * Test Generation Request (Public endpoint for testing)
     */
    @PostMapping("/debug/test-generation")
    public ResponseEntity<Map<String, Object>> testGeneration() {
        try {
            // Test OpenAI config first
            boolean configValid = openAIService.isConfigurationValid();
            boolean connectionTest = openAIService.testConnection();
            
            Map<String, Object> response = new HashMap<>();
            response.put("configValid", configValid);
            response.put("connectionTest", connectionTest);
            
            if (!configValid || !connectionTest) {
                response.put("success", false);
                response.put("error", "OpenAI configuration or connection failed");
                return ResponseEntity.ok(response);
            }
            
            // Test with flashcard prompt
            String testPrompt = "Create a flashcard about Python variables";
            String generatedContent = openAIService.generateContent(testPrompt, "flashcard", null, "test-user");
            
            response.put("success", true);
            response.put("promptText", testPrompt);
            response.put("generatedContent", generatedContent);
            response.put("contentLength", generatedContent != null ? generatedContent.length() : 0);
            response.put("message", "Test generation completed");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            Map<String, Object> response = new HashMap<>();
            response.put("success", false);
            response.put("error", e.getMessage());
            response.put("stackTrace", e.getStackTrace()[0].toString());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
    }
    
    // Request DTOs
    public record AIJobUploadRequest(String fileName, String contentType) {}
    
    public record CreateJobRequest(String jobType, String inputData) {}
    
    public record CreateDraftRequest(String draftType, String title, String content) {}
}

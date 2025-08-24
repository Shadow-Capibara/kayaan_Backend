package se499.kayaanbackend.AI_Generate.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.AI_Generate.entity.AIJob;
import se499.kayaanbackend.AI_Generate.entity.AIDraft;
import se499.kayaanbackend.AI_Generate.service.AIJobService;
import se499.kayaanbackend.AI_Generate.service.OpenAIService;
import se499.kayaanbackend.security.user.User;

import java.util.Map;

@RestController
@RequestMapping("/api/ai")
@RequiredArgsConstructor
@Slf4j
public class AIJobController {
    
    private final AIJobService aiJobService;
    private final OpenAIService openAIService;
    
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
     * ทดสอบ OpenAI Configuration
     */
    @GetMapping("/config/test")
    @PreAuthorize("isAuthenticated()")
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
    
    // Request DTOs
    public record AIJobUploadRequest(String fileName, String contentType) {}
    
    public record CreateJobRequest(String jobType, String inputData) {}
    
    public record CreateDraftRequest(String draftType, String title, String content) {}
}

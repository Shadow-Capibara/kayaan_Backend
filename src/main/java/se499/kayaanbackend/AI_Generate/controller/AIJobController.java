package se499.kayaanbackend.AI_Generate.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se499.kayaanbackend.AI_Generate.service.AIJobService;

@RestController
@RequestMapping("/api/ai/jobs")
public class AIJobController {
    
    @Autowired
    private AIJobService aiJobService;
    
    @PostMapping("/{id}/request-upload")
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
     * Request DTO for AI job upload URL
     */
    public record AIJobUploadRequest(String fileName, String contentType) {}
}

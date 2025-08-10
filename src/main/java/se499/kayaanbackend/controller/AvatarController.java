package se499.kayaanbackend.controller;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.DTO.AvatarDTO;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.service.AvatarService;
import se499.kayaanbackend.shared.storage.StorageService;

@RestController
@RequestMapping("/api/users")
@Slf4j
public class AvatarController {

    private final AvatarService avatarService;
    private final StorageService storageService;
    
    @Value("${kayaan.supabase.buckets.avatars}")
    private String avatarsBucket;
    
    public AvatarController(AvatarService avatarService, StorageService storageService) {
        this.avatarService = avatarService;
        this.storageService = storageService;
        log.info("AvatarController initialized with StorageService: {}", storageService.getClass().getName());
    }
    
    @PostMapping(
        value = "/{id}/avatar-upload-url",
        consumes = MediaType.APPLICATION_JSON_VALUE,
        produces = MediaType.APPLICATION_JSON_VALUE
    )
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> requestAvatarUploadUrl(
            @PathVariable Long id,
            @Valid @RequestBody UploadUrlRequest req,
            BindingResult br
    ) {
        // Handle validation errors
        if (br.hasErrors()) {
            Map<String, String> errors = br.getFieldErrors().stream()
                .collect(Collectors.toMap(
                    FieldError::getField,
                    FieldError::getDefaultMessage
                ));
            
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Validation failed");
            errorResponse.put("details", errors);
            
            return ResponseEntity.badRequest().body(errorResponse);
        }
        
        try {
            // Normalize contentType
            String contentType = (req.contentType() == null || req.contentType().isBlank()) 
                ? "application/octet-stream" 
                : req.contentType();
            
            // Build path
            String path = String.format("users/%d/%d_%s", id, System.currentTimeMillis(), req.fileName());
            
            log.info("Creating upload URL for user {} with fileName: {}, contentType: {}", 
                    id, req.fileName(), contentType);
            
            // Create signed URL
            StorageService.SignedUrl signedUrl = storageService.createSignedUploadUrl(
                avatarsBucket, 
                path, 
                (int) Duration.ofMinutes(10).getSeconds(),
                contentType
            );
            
            Map<String, Object> response = new HashMap<>();
            response.put("signedUrl", signedUrl.url());
            response.put("path", signedUrl.path());
            response.put("expiresIn", signedUrl.expiresInSeconds());
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error creating upload URL for user {}: {}", id, e.getMessage(), e);
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }

    @PutMapping("/{id}/avatar-url")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> updateAvatarUrl(
            @PathVariable Long id,
            @RequestBody AvatarUrlUpdateRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            String publicUrl = storageService.getPublicUrl(avatarsBucket, request.path());
            AvatarDTO dto = avatarService.savePresetAvatar(id, publicUrl, 0);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Request DTO for avatar upload URL with flexible field mapping
     */
    public record UploadUrlRequest(
        @JsonProperty("fileName")
        @JsonAlias({"filename", "name"})
        @NotBlank(message = "fileName must not be blank") 
        String fileName,
        
        @JsonProperty("contentType")
        @JsonAlias({"mimeType", "mimetype", "type"})
        @NotBlank(message = "contentType must not be blank") 
        String contentType
    ) {}
    
    /**
     * Request DTO for avatar URL update
     */
    public record AvatarUrlUpdateRequest(String path) {}
}

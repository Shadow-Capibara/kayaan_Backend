package se499.kayaanbackend.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

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
        log.info("StorageService impl = {}", storageService.getClass().getName());
    }
    
    @PostMapping("/{id}/avatar-upload-url")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> requestAvatarUploadUrl(
            @PathVariable Integer id,
            @RequestBody AvatarUploadUrlRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            String timestamp = String.valueOf(Instant.now().toEpochMilli());
            String path = String.format("users/%d/%s_%s", id, timestamp, request.fileName());
            
            StorageService.SignedUrl signedUrl = storageService.createSignedUploadUrl(
                avatarsBucket, 
                path, 
                600, // 10 minutes
                request.contentType()
            );
            
            return ResponseEntity.ok(Map.of(
                "signedUrl", signedUrl.url(),
                "path", signedUrl.path(),
                "expiresIn", signedUrl.expiresInSeconds()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/avatar-url")
    @PreAuthorize("hasRole('ADMIN') or #id == authentication.principal.id")
    public ResponseEntity<?> updateAvatarUrl(
            @PathVariable Integer id,
            @RequestBody AvatarUrlUpdateRequest request,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            String publicUrl = storageService.getPublicUrl(avatarsBucket, request.path());
            AvatarDTO dto = avatarService.savePresetAvatar(id.longValue(), publicUrl, 0);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
    
    // Legacy endpoint disabled - use new flow: avatar-upload-url -> PUT signed URL -> avatar-url
    @PostMapping("/{id}/avatar-upload")
    @ResponseStatus(HttpStatus.GONE) // 410
    public ResponseEntity<?> legacyUploadDisabled() {
        return ResponseEntity.status(HttpStatus.GONE)
            .body(Map.of("error", "Legacy upload endpoint disabled. Use new flow: POST /avatar-upload-url -> PUT signed URL -> PUT /avatar-url"));
    }
    
    /**
     * Request DTO for avatar upload URL
     */
    public record AvatarUploadUrlRequest(String fileName, String contentType) {}
    
    /**
     * Request DTO for avatar URL update
     */
    public record AvatarUrlUpdateRequest(String path) {}
}

package se499.kayaanbackend.controller;

import java.time.Instant;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;
import se499.kayaanbackend.DTO.AvatarDTO;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.service.AvatarService;
import se499.kayaanbackend.shared.storage.StorageService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;
    private final StorageService storageService;
    
    @Value("${kayaan.supabase.buckets.avatars}")
    private String avatarsBucket;
    
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
    
    // Keep existing endpoint for backward compatibility
    @PostMapping("/{id}/avatar-upload")
    public ResponseEntity<?> uploadAvatar(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @RequestParam("rotation") int rotation,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            AvatarDTO dto = avatarService.storeAvatar(id, file, rotation);
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
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

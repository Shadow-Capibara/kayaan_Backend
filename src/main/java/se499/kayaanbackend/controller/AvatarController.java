package se499.kayaanbackend.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.service.AvatarService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;

    @PostMapping("/{id}/avatar")
    public ResponseEntity<String> uploadAvatar(
            @PathVariable Long id,
            @RequestParam("file") MultipartFile file,
            @AuthenticationPrincipal User currentUser
    ) {
        // ✅ ตรวจสอบว่า user ที่ login ตรงกับ user ที่จะเปลี่ยน avatar
        if (!currentUser.getId().equals(id)) {
            return ResponseEntity.status(403).body("You are not allowed to update this user's avatar.");
        }

        try {
            String avatarUrl = avatarService.storeAvatar(id, file);
            return ResponseEntity.ok(avatarUrl);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to upload avatar");
        }
    }
}
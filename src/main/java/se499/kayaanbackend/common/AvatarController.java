package se499.kayaanbackend.common;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import se499.kayaanbackend.common.AvatarDTO;
import se499.kayaanbackend.security.entity.User;
import se499.kayaanbackend.common.AvatarService;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class AvatarController {

    private final AvatarService avatarService;
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

    @PutMapping("/{id}/avatar-url")
    public ResponseEntity<?> updateAvatarUrl(
            @PathVariable Long id,
            @RequestBody AvatarDTO avatarDTO,
            @AuthenticationPrincipal User currentUser
    ) {
        try {
            AvatarDTO dto = avatarService.savePresetAvatar(id, avatarDTO.getAvatarUrl(), avatarDTO.getRotation());
            return ResponseEntity.ok(dto);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}

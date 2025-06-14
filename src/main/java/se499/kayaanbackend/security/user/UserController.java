package se499.kayaanbackend.security.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<UserInfoDTO> getCurrentUser(@AuthenticationPrincipal User currentUser) {
        UserInfoDTO userInfo = UserInfoDTO.builder()
                .id(currentUser.getId())
                .username(currentUser.getUsername())
                .email(currentUser.getEmail())
                .firstname(currentUser.getFirstname())
                .lastname(currentUser.getLastname())
                .avatarUrl(currentUser.getAvatarUrl())
                .roles(currentUser.getRoles().stream()
                        .map(Enum::name)
                        .toList())
                .build();
        return ResponseEntity.ok(userInfo);
    }
}
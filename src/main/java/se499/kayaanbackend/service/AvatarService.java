package se499.kayaanbackend.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.security.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class AvatarService {

        @Value("${avatar.upload.dir}")
        private String uploadDir;

        private final UserRepository userRepository;

        public String storeAvatar(Long userId, MultipartFile file) throws IOException {
            if (file.isEmpty()) {
                throw new IllegalArgumentException("Empty file");
            }

            String contentType = file.getContentType();
            if (contentType == null ||
                    (!contentType.equals("image/png") && !contentType.equals("image/jpeg"))) {
                throw new IllegalArgumentException("Invalid file type (only PNG/JPEG allowed)");
            }

            if (file.getSize() > 2 * 1024 * 1024) {
                throw new IllegalArgumentException("File too large (max 2MB)");
            }

            // Find user
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Generate new file name
            String extension = contentType.equals("image/png") ? ".png" : ".jpg";
            String filename = "user_" + userId + extension;
            Path destinationPath = Paths.get(uploadDir).resolve(filename);
            Files.createDirectories(destinationPath.getParent());

            // Save file
            file.transferTo(destinationPath.toFile());

            // Update user avatar URL
            String avatarUrl = "/uploads/avatars/" + filename;
            user.setAvatarUrl(avatarUrl);
            userRepository.save(user);

            return avatarUrl;
        }
    }

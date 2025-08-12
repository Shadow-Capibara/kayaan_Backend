package se499.kayaanbackend.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import se499.kayaanbackend.DTO.AvatarDTO;
import se499.kayaanbackend.mapper.AvatarMapper;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.security.user.UserDao;

@Service
@RequiredArgsConstructor
public class AvatarServiceImpl implements AvatarService {

    private final UserDao userDao;
    private final AvatarMapper avatarMapper;

    private final String uploadDir = System.getProperty("user.dir") + "/uploads/avatars";

    @Override
    @Transactional
    public AvatarDTO storeAvatar(Long userId, MultipartFile file, int rotation) throws IOException {
        if (file.isEmpty()) throw new IllegalArgumentException("Empty file");

        String contentType = file.getContentType();
        if (contentType == null || (!contentType.equals("image/png") && !contentType.equals("image/jpeg"))) {
            throw new IllegalArgumentException("Invalid file type");
        }

        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File too large (max 5MB)");
        }

        User user = userDao.findById(userId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        String extension = contentType.equals("image/png") ? ".png" : ".jpg";
        String filename = "user_" + userId + extension;
        Path destinationPath = Paths.get(uploadDir).resolve(filename);
        Files.createDirectories(destinationPath.getParent());

        file.transferTo(destinationPath.toFile());

        user.setAvatarUrl("/uploads/avatars/" + filename);
        userDao.save(user);

        return avatarMapper.toDto(user);
    }

    @Override
    @Transactional
    public AvatarDTO savePresetAvatar(Long userId, String avatarUrl, int rotation) {
        User user = userDao.findById(userId.intValue())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setAvatarUrl(avatarUrl);
        userDao.save(user);

        return avatarMapper.toDto(user);
    }
}

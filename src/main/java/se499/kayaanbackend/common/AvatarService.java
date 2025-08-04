package se499.kayaanbackend.common;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface AvatarService {
    AvatarDTO storeAvatar(Long userId, MultipartFile file, int rotation) throws IOException;
    AvatarDTO savePresetAvatar(Long userId, String avatarUrl, int rotation);
}

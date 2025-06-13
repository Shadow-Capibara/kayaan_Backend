package se499.kayaanbackend.service;

import org.springframework.web.multipart.MultipartFile;
import se499.kayaanbackend.DTO.AvatarDTO;

import java.io.IOException;

public interface AvatarService {
    AvatarDTO storeAvatar(Long userId, MultipartFile file, int rotation) throws IOException;
    AvatarDTO savePresetAvatar(Long userId, String avatarUrl, int rotation);
}

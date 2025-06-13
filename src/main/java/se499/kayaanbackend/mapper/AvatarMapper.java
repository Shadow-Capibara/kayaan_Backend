// AvatarMapper.java
package se499.kayaanbackend.mapper;

import org.springframework.stereotype.Component;
import se499.kayaanbackend.DTO.AvatarDTO;
import se499.kayaanbackend.security.user.User;

@Component
public class AvatarMapper {
    public AvatarDTO toDto(User user) {
        AvatarDTO dto = new AvatarDTO();
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }
}

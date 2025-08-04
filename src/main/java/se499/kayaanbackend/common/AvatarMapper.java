// AvatarMapper.java
package se499.kayaanbackend.common;

import org.springframework.stereotype.Component;

import se499.kayaanbackend.security.entity.User;

@Component
public class AvatarMapper {
    public AvatarDTO toDto(User user) {
        AvatarDTO dto = new AvatarDTO();
        dto.setAvatarUrl(user.getAvatarUrl());
        return dto;
    }
}

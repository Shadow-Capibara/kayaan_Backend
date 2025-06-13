package se499.kayaanbackend.DTO;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class AvatarDTO {
    private Integer userId;
    private String avatarUrl;
    private Integer rotation;
}

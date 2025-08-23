package se499.kayaanbackend.security.user;


import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoDTO {
    private Integer id;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private String avatarUrl;
    private List<String> roles;
}
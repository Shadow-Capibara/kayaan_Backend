// AuthenticationResponse.java
package se499.kayaanbackend.security.auth;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se499.kayaanbackend.security.user.User;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AuthenticationResponse {

  @JsonProperty("access_token")
  private String accessToken;
  @JsonProperty("refresh_token")
  private String refreshToken;
  private String message;
  private UserInfo user;

  @Data
  @Builder
  @AllArgsConstructor
  @NoArgsConstructor
  public static class UserInfo {
    private Integer id;
    private String username;
    private String email;
    private String firstname;
    private String lastname;
    private String avatarUrl;

    public static UserInfo fromUser(User user) {
      return UserInfo.builder()
              .id(user.getId())
              .username(user.getUsername())
              .email(user.getEmail())
              .firstname(user.getFirstname())
              .lastname(user.getLastname())
              .avatarUrl(user.getAvatarUrl())
              .build();
    }
  }
}
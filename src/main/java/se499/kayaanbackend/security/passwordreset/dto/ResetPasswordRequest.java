package se499.kayaanbackend.security.passwordreset.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO สำหรับ request เมื่อผู้ใช้ reset รหัสผ่านด้วยรหัส 6 ตัว
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ResetPasswordRequest {

    @NotBlank(message = "Reset code is required")
    @Size(min = 6, max = 6, message = "Reset code must be exactly 6 characters")
    @Pattern(regexp = "^[a-z0-9]{6}$", message = "Reset code must contain only lowercase letters and numbers")
    private String resetCode;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String newPassword;
}


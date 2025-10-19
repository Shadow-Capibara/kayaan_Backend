package se499.kayaanbackend.security.passwordreset.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO สำหรับ response ของการ reset password
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PasswordResetResponse {

    private boolean success;
    private String message;
    private String email;
    
    /**
     * สร้าง response สำหรับกรณีสำเร็จ
     */
    public static PasswordResetResponse success(String message) {
        return PasswordResetResponse.builder()
                .success(true)
                .message(message)
                .build();
    }

    /**
     * สร้าง response สำหรับกรณีสำเร็จพร้อม email
     */
    public static PasswordResetResponse success(String message, String email) {
        return PasswordResetResponse.builder()
                .success(true)
                .message(message)
                .email(email)
                .build();
    }

    /**
     * สร้าง response สำหรับกรณีผิดพลาด
     */
    public static PasswordResetResponse error(String message) {
        return PasswordResetResponse.builder()
                .success(false)
                .message(message)
                .build();
    }
}


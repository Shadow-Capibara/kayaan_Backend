package se499.kayaanbackend.security.passwordreset;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se499.kayaanbackend.security.user.User;

import java.time.LocalDateTime;

/**
 * Entity class สำหรับเก็บข้อมูล Password Reset Token
 * ใช้ในระบบ Forgot Password
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "password_reset_token")
public class PasswordResetToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    private String email;

    /**
     * รหัส 6 ตัว (a-z, 0-9) สำหรับ reset password
     */
    @Column(name = "reset_code", nullable = false, length = 6)
    private String resetCode;

    /**
     * สถานะว่ารหัสนี้ถูกใช้ไปแล้วหรือยัง
     */
    @Column(name = "is_used", nullable = false)
    @Builder.Default
    private Boolean isUsed = false;

    /**
     * เวลาหมดอายุของรหัส (15 นาที จากเวลาที่สร้าง)
     */
    @Column(name = "expires_at", nullable = false)
    private LocalDateTime expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
    }

    /**
     * เช็คว่ารหัสหมดอายุหรือยัง
     */
    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expiresAt);
    }

    /**
     * เช็คว่ารหัสยังใช้งานได้หรือไม่ (ไม่หมดอายุและยังไม่ถูกใช้)
     */
    public boolean isValid() {
        return !isExpired() && !isUsed;
    }
}


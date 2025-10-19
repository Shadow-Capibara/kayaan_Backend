package se499.kayaanbackend.security.passwordreset;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Repository สำหรับจัดการ PasswordResetToken
 */
@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Integer> {

    /**
     * หา token ที่ยังใช้งานได้จากรหัส reset code
     */
    Optional<PasswordResetToken> findByResetCodeAndIsUsedFalse(String resetCode);

    /**
     * หา token ทั้งหมดของ email
     */
    Optional<PasswordResetToken> findByEmailAndIsUsedFalseAndExpiresAtAfter(
            String email, 
            LocalDateTime currentTime
    );

    /**
     * ลบ token ที่หมดอายุและถูกใช้แล้ว (Cleanup)
     */
    @Modifying
    @Query("DELETE FROM PasswordResetToken p WHERE p.expiresAt < :currentTime OR p.isUsed = true")
    void deleteExpiredAndUsedTokens(LocalDateTime currentTime);

    /**
     * ทำให้ token ทั้งหมดของ email นี้เป็น used (เมื่อสร้าง token ใหม่)
     */
    @Modifying
    @Query("UPDATE PasswordResetToken p SET p.isUsed = true WHERE p.email = :email AND p.isUsed = false")
    void invalidateAllTokensByEmail(String email);
}


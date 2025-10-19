package se499.kayaanbackend.security.passwordreset.service;

import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se499.kayaanbackend.security.passwordreset.PasswordResetToken;
import se499.kayaanbackend.security.passwordreset.PasswordResetTokenRepository;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.security.user.UserRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

/**
 * Service สำหรับจัดการระบบ Forgot Password และ Reset Password
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class PasswordResetService {

    private final PasswordResetTokenRepository tokenRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;

    // ตัวอักษรที่ใช้ในการสร้างรหัส (a-z และ 0-9)
    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyz0123456789";
    private static final int CODE_LENGTH = 6;
    private static final int EXPIRATION_MINUTES = 15;

    /**
     * สร้างและส่งรหัส reset password ไปยังอีเมลของผู้ใช้
     * 
     * @param email อีเมลของผู้ใช้
     * @return true ถ้าส่งสำเร็จ
     * @throws Exception ถ้ามีข้อผิดพลาด
     */
    @Transactional
    public boolean sendPasswordResetCode(String email) throws Exception {
        log.info("Processing password reset request for email: {}", email);

        // 1. ตรวจสอบว่ามี user ที่ใช้อีเมลนี้หรือไม่
        Optional<User> userOptional = userRepository.findByEmail(email);
        if (userOptional.isEmpty()) {
            log.warn("No user found with email: {}", email);
            // ไม่บอก user ว่าไม่มีอีเมลนี้ เพื่อความปลอดภัย
            return true; // ส่งกลับว่าสำเร็จเพื่อไม่ให้คนนอกรู้ว่าอีเมลนี้มีในระบบหรือไม่
        }

        User user = userOptional.get();
        log.info("User found: {} (ID: {})", user.getEmail(), user.getId());

        // 2. ทำให้ token เก่าทั้งหมดของอีเมลนี้เป็น invalid
        tokenRepository.invalidateAllTokensByEmail(email);
        log.info("Invalidated all previous tokens for email: {}", email);

        // 3. สร้างรหัส reset code 6 ตัว (a-z, 0-9)
        String resetCode = generateResetCode();
        log.info("Generated reset code for email: {}", email);

        // 4. คำนวณเวลาหมดอายุ (15 นาที)
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(EXPIRATION_MINUTES);

        // 5. บันทึก token ลงฐานข้อมูล
        PasswordResetToken token = PasswordResetToken.builder()
                .user(user)
                .email(email)
                .resetCode(resetCode)
                .isUsed(false)
                .expiresAt(expiresAt)
                .build();

        tokenRepository.save(token);
        log.info("Password reset token saved to database for email: {}", email);

        // 6. ส่งอีเมลพร้อมรหัส reset
        try {
            emailService.sendPasswordResetEmail(email, resetCode);
            log.info("Password reset email sent successfully to: {}", email);
            return true;
        } catch (MessagingException e) {
            log.error("Failed to send password reset email to: {}", email, e);
            throw new Exception("Failed to send email. Please try again later.");
        }
    }

    /**
     * ตรวจสอบรหัส reset และเปลี่ยนรหัสผ่านใหม่
     * 
     * @param resetCode รหัส reset 6 ตัว
     * @param newPassword รหัสผ่านใหม่
     * @return true ถ้าสำเร็จ
     * @throws Exception ถ้ามีข้อผิดพลาด
     */
    @Transactional
    public boolean resetPassword(String resetCode, String newPassword) throws Exception {
        log.info("Processing password reset with code: {}", resetCode);

        // 1. หา token จากรหัส reset code
        Optional<PasswordResetToken> tokenOptional = 
                tokenRepository.findByResetCodeAndIsUsedFalse(resetCode);

        if (tokenOptional.isEmpty()) {
            log.warn("Invalid or already used reset code: {}", resetCode);
            throw new Exception("Invalid or expired reset code");
        }

        PasswordResetToken token = tokenOptional.get();

        // 2. ตรวจสอบว่า token หมดอายุหรือไม่
        if (token.isExpired()) {
            log.warn("Reset code expired: {}", resetCode);
            throw new Exception("Reset code has expired. Please request a new one.");
        }

        // 3. ตรวจสอบว่า token ยังใช้งานได้หรือไม่
        if (!token.isValid()) {
            log.warn("Reset code is not valid: {}", resetCode);
            throw new Exception("Invalid reset code");
        }

        // 4. เปลี่ยนรหัสผ่านของ user
        User user = token.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password updated successfully for user ID: {}", user.getId());

        // 5. ทำเครื่องหมายว่า token ถูกใช้แล้ว
        token.setIsUsed(true);
        tokenRepository.save(token);
        log.info("Reset token marked as used: {}", resetCode);

        return true;
    }

    /**
     * สร้างรหัส reset code แบบสุ่ม 6 ตัว (a-z, 0-9)
     * 
     * @return รหัส 6 ตัว
     */
    private String generateResetCode() {
        SecureRandom random = new SecureRandom();
        StringBuilder code = new StringBuilder(CODE_LENGTH);

        for (int i = 0; i < CODE_LENGTH; i++) {
            int index = random.nextInt(CHARACTERS.length());
            code.append(CHARACTERS.charAt(index));
        }

        return code.toString();
    }

    /**
     * ลบ token ที่หมดอายุและถูกใช้แล้วออกจากฐานข้อมูล (Cleanup)
     * ควรเรียกใช้เป็นระยะๆ (เช่น ทุกวัน)
     */
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Starting cleanup of expired and used password reset tokens");
        tokenRepository.deleteExpiredAndUsedTokens(LocalDateTime.now());
        log.info("Cleanup completed");
    }
}


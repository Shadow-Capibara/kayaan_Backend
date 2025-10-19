package se499.kayaanbackend.security.passwordreset.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

/**
 * Service สำหรับส่งอีเมล
 * ใช้ Spring Mail กับ Gmail SMTP
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;

    /**
     * ส่งอีเมล reset password พร้อมรหัส 6 ตัว
     * 
     * @param toEmail อีเมลผู้รับ
     * @param resetCode รหัส reset 6 ตัว
     * @throws MessagingException ถ้าส่งอีเมลไม่สำเร็จ
     */
    public void sendPasswordResetEmail(String toEmail, String resetCode) throws MessagingException {
        log.info("Preparing to send password reset email to: {}", toEmail);

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

        helper.setFrom(fromEmail);
        helper.setTo(toEmail);
        helper.setSubject("รหัสสำหรับรีเซ็ตรหัสผ่าน - Kayaan");

        String emailContent = buildPasswordResetEmailContent(resetCode);
        helper.setText(emailContent, true); // true = HTML content

        mailSender.send(message);
        log.info("Password reset email sent successfully to: {}", toEmail);
    }

    /**
     * สร้าง HTML content สำหรับอีเมล reset password
     * ใช้ String.format() กับ %s (ปลอดภัย)
     */
    private String buildPasswordResetEmailContent(String resetCode) {
        // ✅ ใช้ String.format() กับ %s เท่านั้น (ปลอดภัย)
        return String.format(
            """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
            </head>
            <body style="font-family: Arial, sans-serif; line-height: 1.6; color: #333; margin: 0; padding: 0; background-color: #f4f4f4;">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    <h2 style="color: #4F46E5; margin-bottom: 20px;">🔐 รีเซ็ตรหัสผ่าน Kayaan</h2>
                    
                    <p style="margin-bottom: 15px;">สวัสดีครับ,</p>
                    
                    <p style="margin-bottom: 20px;">คุณได้ทำการร้องขอรีเซ็ตรหัสผ่านสำหรับบัญชี Kayaan ของคุณ กรุณาใช้รหัสด้านล่างนี้เพื่อดำเนินการต่อ:</p>
                    
                    <div style="background-color: #F3F4F6; padding: 20px; border-radius: 8px; text-align: center; margin: 20px 0; border: 2px solid #4F46E5;">
                        <h1 style="font-family: 'Courier New', monospace; letter-spacing: 8px; color: #4F46E5; font-size: 32px; margin: 0;">%s</h1>
                    </div>
                    
                    <div style="background-color: #FEF3C7; border-left: 4px solid #F59E0B; padding: 12px; margin: 20px 0; border-radius: 4px;">
                        <p style="margin: 0;"><strong>⏰ สำคัญ:</strong> รหัสนี้จะหมดอายุภายใน <strong>15 นาที</strong></p>
                    </div>
                    
                    <p style="margin-bottom: 20px;">กรุณานำรหัสนี้ไปกรอกในหน้า Reset Password พร้อมกับรหัสผ่านใหม่ที่คุณต้องการ</p>
                    
                    <div style="background-color: #FEE2E2; border-left: 4px solid #EF4444; padding: 12px; margin: 20px 0; border-radius: 4px;">
                        <p style="margin: 0;"><strong>🔒 หมายเหตุ:</strong> หากคุณไม่ได้ทำการร้องขอรีเซ็ตรหัสผ่าน กรุณาเพิกเฉยต่ออีเมลนี้</p>
                    </div>
                    
                    <hr style="border: none; border-top: 1px solid #E5E7EB; margin: 30px 0;">
                    
                    <p style="color: #6B7280; font-size: 14px; margin-bottom: 5px;">
                        ขอบคุณครับ,<br>
                        <strong>Kayaan Team</strong>
                    </p>
                    
                    <p style="color: #9CA3AF; font-size: 12px; margin-top: 20px;">
                        © 2025 Kayaan. All rights reserved.<br>
                        อีเมลนี้ถูกส่งโดยอัตโนมัติ กรุณาอย่าตอบกลับ
                    </p>
                </div>
            </body>
            </html>
            """,
            resetCode  // ใช้ %s เท่านั้น - ปลอดภัย
        );
    }
}


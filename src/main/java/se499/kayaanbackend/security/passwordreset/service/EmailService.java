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
     */
    private String buildPasswordResetEmailContent(String resetCode) {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                    <meta charset="UTF-8">
                    <style>
                        body {
                            font-family: 'Segoe UI', Tahoma, Geneva, Verdana, sans-serif;
                            background-color: #f4f4f4;
                            margin: 0;
                            padding: 0;
                        }
                        .container {
                            max-width: 600px;
                            margin: 40px auto;
                            background-color: #ffffff;
                            border-radius: 10px;
                            box-shadow: 0 4px 6px rgba(0,0,0,0.1);
                            overflow: hidden;
                        }
                        .header {
                            background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
                            color: white;
                            padding: 30px;
                            text-align: center;
                        }
                        .header h1 {
                            margin: 0;
                            font-size: 28px;
                        }
                        .content {
                            padding: 40px 30px;
                        }
                        .reset-code-box {
                            background-color: #f8f9fa;
                            border: 2px dashed #667eea;
                            border-radius: 8px;
                            padding: 25px;
                            text-align: center;
                            margin: 25px 0;
                        }
                        .reset-code {
                            font-size: 36px;
                            font-weight: bold;
                            color: #667eea;
                            letter-spacing: 8px;
                            font-family: 'Courier New', monospace;
                        }
                        .info-text {
                            color: #555;
                            line-height: 1.6;
                            margin: 15px 0;
                        }
                        .warning {
                            background-color: #fff3cd;
                            border-left: 4px solid #ffc107;
                            padding: 15px;
                            margin: 20px 0;
                            border-radius: 4px;
                        }
                        .footer {
                            background-color: #f8f9fa;
                            padding: 20px;
                            text-align: center;
                            color: #6c757d;
                            font-size: 14px;
                        }
                    </style>
                </head>
                <body>
                    <div class="container">
                        <div class="header">
                            <h1>🔐 รีเซ็ตรหัสผ่าน Kayaan</h1>
                        </div>
                        <div class="content">
                            <p class="info-text">สวัสดีครับ,</p>
                            <p class="info-text">
                                คุณได้ทำการร้องขอรีเซ็ตรหัสผ่านสำหรับบัญชี Kayaan ของคุณ 
                                กรุณาใช้รหัสด้านล่างนี้เพื่อดำเนินการต่อ:
                            </p>
                            
                            <div class="reset-code-box">
                                <div style="color: #6c757d; font-size: 14px; margin-bottom: 10px;">
                                    รหัสรีเซ็ตรหัสผ่านของคุณคือ
                                </div>
                                <div class="reset-code">%s</div>
                            </div>
                            
                            <div class="warning">
                                <strong>⏰ สำคัญ:</strong> รหัสนี้จะหมดอายุภายใน <strong>15 นาที</strong>
                            </div>
                            
                            <p class="info-text">
                                หากคุณไม่ได้ทำการร้องขอรีเซ็ตรหัสผ่าน กรุณาเพิกเฉยต่ออีเมลนี้ 
                                บัญชีของคุณยังคงปลอดภัยอยู่
                            </p>
                            
                            <p class="info-text" style="margin-top: 30px;">
                                ขอบคุณที่ใช้บริการ Kayaan<br>
                                ทีมงาน Kayaan
                            </p>
                        </div>
                        <div class="footer">
                            © 2025 Kayaan. All rights reserved.<br>
                            อีเมลนี้ถูกส่งโดยอัตโนมัติ กรุณาอย่าตอบกลับ
                        </div>
                    </div>
                </body>
                </html>
                """.formatted(resetCode);
    }
}


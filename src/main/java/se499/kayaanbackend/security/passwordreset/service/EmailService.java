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
        helper.setSubject("Password Reset Code - Kayaan");

        String emailContent = buildPasswordResetEmailContent(resetCode);
        helper.setText(emailContent, true); // true = HTML content

        mailSender.send(message);
        log.info("Password reset email sent successfully to: {}", toEmail);
    }

    /**
     * Build HTML content for password reset email
     * Using String.format() with %s (safe)
     * 🎨 Design following Kayaan Brand Guidelines:
     *    - Primary Color: #7e69ab (Purple)
     *    - Secondary Color: #1eaedb (Blue)
     *    - Gradient: #3b82f6 → #6366f1 (Blue to Indigo)
     *    - Logo: Kayaan Minimal Face with sparkles & blush
     */
    private String buildPasswordResetEmailContent(String resetCode) {
        // ✅ Using String.format() with %s only (safe)
        return String.format(
            """
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <title>Password Reset - Kayaan</title>
            </head>
            <body style="font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif; line-height: 1.6; color: #1e293b; margin: 0; padding: 0; background: linear-gradient(135deg, #f8fafc 0%%, #e2e8f0 100%%);">
                <div style="max-width: 600px; margin: 0 auto; padding: 20px;">
                    
                    <!-- Header with Kayaan Minimal Face Logo -->
                    <div style="text-align: center; margin-bottom: 40px; padding: 40px 20px; background: linear-gradient(135deg, #3b82f6, #6366f1); border-radius: 20px; box-shadow: 0 10px 30px rgba(59, 130, 246, 0.25); position: relative; overflow: hidden;">
                        <!-- Background Decorative Elements -->
                        <div style="position: absolute; top: -20px; right: -20px; width: 100px; height: 100px; background: rgba(255,255,255,0.1); border-radius: 50%%; filter: blur(40px);"></div>
                        <div style="position: absolute; bottom: -30px; left: -30px; width: 120px; height: 120px; background: rgba(99, 102, 241, 0.3); border-radius: 50%%; filter: blur(50px);"></div>
                        
                        <!-- Logo Container -->
                        <div style="display: inline-block; position: relative; z-index: 1;">
                            <div style="display: inline-flex; align-items: center; background: rgba(255,255,255,0.15); padding: 16px 24px; border-radius: 16px; backdrop-filter: blur(10px); border: 2px solid rgba(255,255,255,0.2); box-shadow: 0 8px 32px rgba(0,0,0,0.1);">
                                
                                <!-- Kayaan Minimal Face Logo (SVG) -->
                                <div style="width: 48px; height: 48px; background: white; border-radius: 50%%; display: inline-flex; align-items: center; justify-content: center; margin-right: 16px; box-shadow: 0 4px 12px rgba(0,0,0,0.1); position: relative; overflow: visible;">
                                    <svg width="48" height="48" viewBox="0 0 48 48" style="display: block;">
                                        <!-- Background Circle -->
                                        <circle cx="24" cy="24" r="24" fill="white"/>
                                        
                                        <!-- Sparkle (top-left outside) -->
                                        <circle cx="6" cy="6" r="1.5" fill="#fcd34d" opacity="0.8"/>
                                        
                                        <!-- Sparkle (top-right outside) -->
                                        <circle cx="42" cy="8" r="1" fill="#f9a8d4" opacity="0.7"/>
                                        
                                        <!-- Cheeks (Blush) -->
                                        <circle cx="13" cy="27" r="2.5" fill="#fbb6ce" opacity="0.4"/>
                                        <circle cx="35" cy="27" r="2.5" fill="#fbb6ce" opacity="0.4"/>
                                        
                                        <!-- Eyes -->
                                        <ellipse cx="18" cy="20" rx="2.5" ry="3" fill="#475569"/>
                                        <ellipse cx="30" cy="20" rx="2.5" ry="3" fill="#475569"/>
                                        
                                        <!-- Eye highlights -->
                                        <circle cx="18.5" cy="19" r="0.8" fill="white" opacity="0.9"/>
                                        <circle cx="30.5" cy="19" r="0.8" fill="white" opacity="0.9"/>
                                        
                                        <!-- Smile (curved path) -->
                                        <path d="M 16 28 Q 24 33 32 28" stroke="#475569" stroke-width="2.5" fill="none" stroke-linecap="round"/>
                                    </svg>
                                </div>
                                
                                <!-- Brand Text -->
                                <div style="text-align: left;">
                                    <h1 style="color: white; margin: 0; font-size: 32px; font-weight: 700; letter-spacing: -0.5px; text-shadow: 0 2px 10px rgba(0,0,0,0.1);">Kayaan</h1>
                                    <p style="color: rgba(255,255,255,0.85); margin: 0; font-size: 14px; font-weight: 500; letter-spacing: 0.5px;">Learning Hub</p>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Main Content Card -->
                    <div style="background: white; border-radius: 20px; padding: 48px; box-shadow: 0 4px 20px rgba(0,0,0,0.08); border: 1px solid #e2e8f0;">
                        
                        <!-- Title -->
                        <div style="text-align: center; margin-bottom: 32px;">
                            <div style="display: inline-block; background: linear-gradient(135deg, #ede9fe, #ddd6fe); padding: 12px 24px; border-radius: 12px; margin-bottom: 16px;">
                                <!-- Lock Icon SVG -->
                                <svg width="32" height="32" viewBox="0 0 24 24" fill="none" stroke="#7e69ab" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <rect x="3" y="11" width="18" height="11" rx="2" ry="2"></rect>
                                    <path d="M7 11V7a5 5 0 0 1 10 0v4"></path>
                                </svg>
                            </div>
                            <h2 style="color: #7e69ab; margin: 0; font-size: 28px; font-weight: 700;">Password Reset Request</h2>
                            <p style="color: #64748b; margin: 8px 0 0 0; font-size: 15px;">Secure your Kayaan account</p>
                        </div>
                        
                        <!-- Greeting -->
                        <p style="margin-bottom: 20px; color: #64748b; font-size: 16px;">
                            Hello there! 
                            <!-- Wave Hand SVG -->
                            <svg style="display: inline-block; width: 20px; height: 20px; vertical-align: middle; margin-left: 4px;" viewBox="0 0 24 24" fill="#fbbf24">
                                <path d="M7.5 6.5C7.5 8.981 9.519 11 12 11s4.5-2.019 4.5-4.5S14.481 2 12 2 7.5 4.019 7.5 6.5zM20 21h1v-1c0-3.859-3.141-7-7-7h-4c-3.86 0-7 3.141-7 7v1h17z"/>
                            </svg>
                        </p>
                        
                        <!-- Message -->
                        <p style="margin-bottom: 32px; color: #334155; font-size: 16px; line-height: 1.7;">
                            We received a request to reset the password for your <strong style="color: #7e69ab;">Kayaan</strong> account. 
                            Please use the verification code below to proceed:
                        </p>
                        
                        <!-- Reset Code Box -->
                        <div style="background: linear-gradient(135deg, #faf5ff, #f3f4f6); padding: 36px; border-radius: 16px; text-align: center; margin: 36px 0; border: 3px solid #7e69ab; position: relative; overflow: hidden; box-shadow: 0 4px 16px rgba(126, 105, 171, 0.15);">
                            <!-- Top Gradient Bar -->
                            <div style="position: absolute; top: 0; left: 0; right: 0; height: 5px; background: linear-gradient(90deg, #7e69ab 0%%, #3b82f6 50%%, #1eaedb 100%%);"></div>
                            
                            <!-- Decorative Background -->
                            <div style="position: absolute; top: 10px; right: 10px; width: 60px; height: 60px; background: radial-gradient(circle, rgba(126, 105, 171, 0.1) 0%%, transparent 70%%); border-radius: 50%%;"></div>
                            <div style="position: absolute; bottom: 10px; left: 10px; width: 80px; height: 80px; background: radial-gradient(circle, rgba(59, 130, 246, 0.1) 0%%, transparent 70%%); border-radius: 50%%;"></div>
                            
                            <!-- Code -->
                            <div style="position: relative; z-index: 1;">
                                <h1 style="font-family: 'SF Mono', 'Monaco', 'Inconsolata', 'Roboto Mono', 'Courier New', monospace; letter-spacing: 16px; color: #7e69ab; font-size: 42px; margin: 0 0 12px 0; font-weight: 700; text-shadow: 0 2px 4px rgba(126, 105, 171, 0.1);">%s</h1>
                                <p style="margin: 0; color: #64748b; font-size: 14px; font-weight: 600; text-transform: uppercase; letter-spacing: 1px;">Verification Code</p>
                            </div>
                        </div>
                        
                        <!-- Expiration Notice -->
                        <div style="background: linear-gradient(135deg, #fef3c7, #fde68a); border-left: 5px solid #f59e0b; padding: 20px 24px; margin: 32px 0; border-radius: 12px; box-shadow: 0 2px 12px rgba(245, 158, 11, 0.15);">
                            <div style="display: flex; align-items: flex-start;">
                                <!-- Clock Icon SVG -->
                                <svg style="width: 24px; height: 24px; margin-right: 12px; flex-shrink: 0;" viewBox="0 0 24 24" fill="none" stroke="#92400e" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <circle cx="12" cy="12" r="10"></circle>
                                    <polyline points="12 6 12 12 16 14"></polyline>
                                </svg>
                                <div>
                                    <p style="margin: 0; color: #92400e; font-weight: 700; font-size: 16px; margin-bottom: 4px;">Time-Sensitive Code</p>
                                    <p style="margin: 0; color: #b45309; font-size: 14px; line-height: 1.6;">
                                        This verification code will <strong>expire in 15 minutes</strong>. Please complete your password reset soon.
                                    </p>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Instructions -->
                        <div style="background: #f8fafc; padding: 24px; border-radius: 12px; margin: 32px 0; border: 1px solid #e2e8f0;">
                            <div style="display: flex; align-items: center; margin-bottom: 12px;">
                                <!-- List Icon SVG -->
                                <svg style="width: 18px; height: 18px; margin-right: 8px;" viewBox="0 0 24 24" fill="none" stroke="#475569" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <line x1="8" y1="6" x2="21" y2="6"></line>
                                    <line x1="8" y1="12" x2="21" y2="12"></line>
                                    <line x1="8" y1="18" x2="21" y2="18"></line>
                                    <line x1="3" y1="6" x2="3.01" y2="6"></line>
                                    <line x1="3" y1="12" x2="3.01" y2="12"></line>
                                    <line x1="3" y1="18" x2="3.01" y2="18"></line>
                                </svg>
                                <p style="margin: 0; color: #475569; font-size: 15px; font-weight: 600;">Next Steps:</p>
                            </div>
                            <ol style="margin: 0; padding-left: 20px; color: #334155; font-size: 15px; line-height: 1.8;">
                                <li>Go to the Password Reset page</li>
                                <li>Enter the 6-digit code above</li>
                                <li>Create your new password</li>
                                <li>Sign in with your new credentials</li>
                            </ol>
                        </div>
                        
                        <!-- Security Notice -->
                        <div style="background: linear-gradient(135deg, #fef2f2, #fee2e2); border-left: 5px solid #ef4444; padding: 20px 24px; margin: 32px 0; border-radius: 12px; box-shadow: 0 2px 12px rgba(239, 68, 68, 0.15);">
                            <div style="display: flex; align-items: flex-start;">
                                <!-- Shield Alert Icon SVG -->
                                <svg style="width: 24px; height: 24px; margin-right: 12px; flex-shrink: 0;" viewBox="0 0 24 24" fill="none" stroke="#991b1b" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M12 22s8-4 8-10V5l-8-3-8 3v7c0 6 8 10 8 10z"></path>
                                    <line x1="12" y1="8" x2="12" y2="12"></line>
                                    <line x1="12" y1="16" x2="12.01" y2="16"></line>
                                </svg>
                                <div>
                                    <p style="margin: 0; color: #991b1b; font-weight: 700; font-size: 16px; margin-bottom: 4px;">Security Alert</p>
                                    <p style="margin: 0; color: #b91c1c; font-size: 14px; line-height: 1.6;">
                                        If you <strong>did not request</strong> this password reset, please ignore this email. 
                                        Your account remains secure and no changes will be made.
                                    </p>
                                </div>
                            </div>
                        </div>
                        
                        <!-- Help Section -->
                        <div style="text-align: center; margin-top: 40px; padding-top: 32px; border-top: 2px solid #e2e8f0;">
                            <p style="color: #64748b; font-size: 14px; margin: 0;">
                                Need help? Contact our support team at 
                                <a href="mailto:support@kayaan.com" style="color: #7e69ab; text-decoration: none; font-weight: 600;">support@kayaan.com</a>
                            </p>
                        </div>
                    </div>
                    
                    <!-- Footer -->
                    <div style="text-align: center; margin-top: 40px; padding: 32px 0;">
                        <div style="background: white; border-radius: 16px; padding: 28px; box-shadow: 0 2px 12px rgba(0,0,0,0.05); border: 1px solid #e2e8f0;">
                            <!-- Thank You Message -->
                            <p style="color: #64748b; font-size: 16px; margin: 0 0 8px 0; font-weight: 500; display: flex; align-items: center; justify-content: center;">
                                Thank you for being part of the Kayaan community! 
                                <!-- Book Icon SVG -->
                                <svg style="width: 20px; height: 20px; margin-left: 6px;" viewBox="0 0 24 24" fill="none" stroke="#7e69ab" stroke-width="2" stroke-linecap="round" stroke-linejoin="round">
                                    <path d="M2 3h6a4 4 0 0 1 4 4v14a3 3 0 0 0-3-3H2z"></path>
                                    <path d="M22 3h-6a4 4 0 0 0-4 4v14a3 3 0 0 1 3-3h7z"></path>
                                </svg>
                            </p>
                            <p style="margin: 0 0 20px 0;">
                                <strong style="background: linear-gradient(90deg, #7e69ab, #3b82f6); -webkit-background-clip: text; -webkit-text-fill-color: transparent; background-clip: text; font-size: 20px; font-weight: 700;">The Kayaan Team</strong>
                            </p>
                            
                            <!-- Social Links (Optional) -->
                            <div style="margin: 20px 0; padding: 20px 0; border-top: 1px solid #e2e8f0; border-bottom: 1px solid #e2e8f0;">
                                <p style="color: #94a3b8; font-size: 13px; margin: 0;">
                                    Stay connected with us for learning tips and updates
                                </p>
                            </div>
                            
                            <!-- Copyright -->
                            <div style="margin-top: 20px;">
                                <p style="color: #94a3b8; font-size: 12px; margin: 0; line-height: 1.6;">
                                    © 2025 <strong style="color: #7e69ab;">Kayaan Learning Hub</strong>. All rights reserved.<br>
                                    <span style="font-size: 11px;">This is an automated email. Please do not reply to this message.</span>
                                </p>
                            </div>
                        </div>
                    </div>
                    
                </div>
            </body>
            </html>
            """,
            resetCode  // Using %s only - safe
        );
    }
}


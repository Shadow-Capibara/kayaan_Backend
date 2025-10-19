package se499.kayaanbackend.security.passwordreset.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.security.passwordreset.dto.ForgotPasswordRequest;
import se499.kayaanbackend.security.passwordreset.dto.PasswordResetResponse;
import se499.kayaanbackend.security.passwordreset.dto.ResetPasswordRequest;
import se499.kayaanbackend.security.passwordreset.service.PasswordResetService;

/**
 * REST Controller สำหรับ Password Reset (Forgot Password)
 * 
 * Endpoints:
 * - POST /api/auth/forgot-password - ส่งรหัส reset ไปยังอีเมล
 * - POST /api/auth/reset-password - เปลี่ยนรหัสผ่านด้วยรหัส 6 ตัว
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Password Reset", description = "API สำหรับจัดการ Forgot Password และ Reset Password")
public class PasswordResetController {

    private final PasswordResetService passwordResetService;

    /**
     * Endpoint สำหรับขอรหัส reset password
     * ระบบจะส่งรหัส 6 ตัว (a-z, 0-9) ไปยังอีเมลของผู้ใช้
     * 
     * @param request ประกอบด้วย email ของผู้ใช้
     * @return response พร้อมสถานะการส่งอีเมล
     */
    @PostMapping("/forgot-password")
    @Operation(
        summary = "ขอรหัส Reset Password",
        description = "ส่งรหัส reset password 6 ตัว (a-z, 0-9) ไปยังอีเมลของผู้ใช้ รหัสจะหมดอายุภายใน 15 นาที"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "ส่งรหัส reset ไปยังอีเมลเรียบร้อยแล้ว",
            content = @Content(schema = @Schema(implementation = PasswordResetResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "ข้อมูลไม่ถูกต้อง",
            content = @Content(schema = @Schema(implementation = PasswordResetResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "เกิดข้อผิดพลาดในการส่งอีเมล",
            content = @Content(schema = @Schema(implementation = PasswordResetResponse.class))
        )
    })
    public ResponseEntity<PasswordResetResponse> forgotPassword(
            @Valid @RequestBody ForgotPasswordRequest request
    ) {
        try {
            log.info("Received forgot password request for email: {}", request.getEmail());
            
            boolean success = passwordResetService.sendPasswordResetCode(request.getEmail());
            
            if (success) {
                return ResponseEntity.ok(
                    PasswordResetResponse.success(
                        "หากอีเมลนี้มีในระบบ เราได้ส่งรหัส reset ไปยังอีเมลของคุณแล้ว กรุณาตรวจสอบอีเมล",
                        request.getEmail()
                    )
                );
            } else {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(PasswordResetResponse.error("ไม่สามารถส่งอีเมลได้ กรุณาลองใหม่อีกครั้ง"));
            }
            
        } catch (Exception e) {
            log.error("Error in forgot password process", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(PasswordResetResponse.error("เกิดข้อผิดพลาด: " + e.getMessage()));
        }
    }

    /**
     * Endpoint สำหรับ reset password ด้วยรหัส 6 ตัว
     * 
     * @param request ประกอบด้วย resetCode (6 ตัว) และ newPassword
     * @return response พร้อมสถานะการเปลี่ยนรหัสผ่าน
     */
    @PostMapping("/reset-password")
    @Operation(
        summary = "Reset Password ด้วยรหัส 6 ตัว",
        description = "เปลี่ยนรหัสผ่านใหม่โดยใช้รหัส reset 6 ตัวที่ได้รับทางอีเมล"
    )
    @ApiResponses(value = {
        @ApiResponse(
            responseCode = "200",
            description = "เปลี่ยนรหัสผ่านสำเร็จ",
            content = @Content(schema = @Schema(implementation = PasswordResetResponse.class))
        ),
        @ApiResponse(
            responseCode = "400",
            description = "รหัส reset ไม่ถูกต้องหรือหมดอายุ",
            content = @Content(schema = @Schema(implementation = PasswordResetResponse.class))
        ),
        @ApiResponse(
            responseCode = "500",
            description = "เกิดข้อผิดพลาดในการเปลี่ยนรหัสผ่าน",
            content = @Content(schema = @Schema(implementation = PasswordResetResponse.class))
        )
    })
    public ResponseEntity<PasswordResetResponse> resetPassword(
            @Valid @RequestBody ResetPasswordRequest request
    ) {
        try {
            log.info("Received reset password request with code: {}", request.getResetCode());
            
            boolean success = passwordResetService.resetPassword(
                request.getResetCode(),
                request.getNewPassword()
            );
            
            if (success) {
                return ResponseEntity.ok(
                    PasswordResetResponse.success("เปลี่ยนรหัสผ่านสำเร็จ คุณสามารถเข้าสู่ระบบด้วยรหัสผ่านใหม่ได้แล้ว")
                );
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(PasswordResetResponse.error("ไม่สามารถเปลี่ยนรหัสผ่านได้"));
            }
            
        } catch (Exception e) {
            log.error("Error in reset password process", e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(PasswordResetResponse.error(e.getMessage()));
        }
    }

    /**
     * Endpoint สำหรับทดสอบว่าระบบทำงานหรือไม่
     */
    @GetMapping("/password-reset/health")
    @Operation(
        summary = "Health Check",
        description = "ตรวจสอบว่า Password Reset API ทำงานปกติหรือไม่"
    )
    public ResponseEntity<PasswordResetResponse> healthCheck() {
        return ResponseEntity.ok(
            PasswordResetResponse.success("Password Reset API is running")
        );
    }
}


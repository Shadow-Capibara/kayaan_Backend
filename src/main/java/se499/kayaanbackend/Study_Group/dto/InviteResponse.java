package se499.kayaanbackend.Study_Group.dto;

import java.time.LocalDateTime;

public record InviteResponse(
    String inviteCode,      // รหัสเชิญที่ใช้ได้
    String token,           // token สำหรับ backward compatibility
    LocalDateTime expiresAt, // วันหมดอายุ
    Integer maxUses,        // จำนวนครั้งที่ใช้ได้สูงสุด
    Integer currentUses,    // จำนวนครั้งที่ใช้ไปแล้ว
    Boolean isActive        // สถานะการใช้งาน
) {
    // Constructor สำหรับ backward compatibility
    public InviteResponse(String token, LocalDateTime expiresAt) {
        this(token, token, expiresAt, null, 0, true);
    }
    
    // Constructor สำหรับ invite code
    public InviteResponse(String inviteCode, LocalDateTime expiresAt, Integer maxUses, Integer currentUses) {
        this(inviteCode, inviteCode, expiresAt, maxUses, currentUses, true);
    }
}

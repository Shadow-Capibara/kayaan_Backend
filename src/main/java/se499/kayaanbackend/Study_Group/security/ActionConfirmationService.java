package se499.kayaanbackend.Study_Group.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.Study_Group.security.ActionConfirmationService.ConfirmationToken;
import se499.kayaanbackend.Study_Group.security.ActionConfirmationService.ConfirmationAction;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service สำหรับการยืนยันการกระทำสำคัญ
 */
@Service
public class ActionConfirmationService {
    
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int TOKEN_LENGTH = 16;
    private static final SecureRandom RANDOM = new SecureRandom();
    
    // เก็บ token ที่ใช้งาน (ใน production ควรใช้ Redis หรือ database)
    private final Map<String, ConfirmationToken> tokenStore = new ConcurrentHashMap<>();
    
    @Autowired
    private GroupPermissionService permissionService;
    
    /**
     * สร้างการยืนยันสำหรับการกระทำสำคัญ
     */
    public ConfirmationToken createConfirmation(Long userId, ConfirmationAction action, Map<String, Object> params) {
        String token = generateSecureToken();
        LocalDateTime expiresAt = LocalDateTime.now().plusMinutes(15); // หมดอายุใน 15 นาที
        
        ConfirmationToken confirmationToken = new ConfirmationToken(
            token, userId, action, params, expiresAt, false
        );
        
        tokenStore.put(token, confirmationToken);
        return confirmationToken;
    }
    
    /**
     * ตรวจสอบการยืนยัน
     */
    public boolean validateConfirmation(String token, ConfirmationAction action) {
        ConfirmationToken confirmationToken = tokenStore.get(token);
        
        if (confirmationToken == null) {
            return false;
        }
        
        if (confirmationToken.isUsed()) {
            return false;
        }
        
        if (confirmationToken.getExpiresAt().isBefore(LocalDateTime.now())) {
            tokenStore.remove(token);
            return false;
        }
        
        if (confirmationToken.getAction() != action) {
            return false;
        }
        
        return true;
    }
    
    /**
     * ใช้ token และลบออกจาก store
     */
    public boolean consumeToken(String token) {
        ConfirmationToken confirmationToken = tokenStore.get(token);
        
        if (confirmationToken != null && !confirmationToken.isUsed()) {
            confirmationToken.setUsed(true);
            tokenStore.remove(token);
            return true;
        }
        
        return false;
    }
    
    /**
     * ยืนยันการลบสมาชิก
     */
    public boolean confirmMemberRemoval(Long userId, Long memberId, Long groupId, String confirmationToken) {
        // ตรวจสอบสิทธิ์
        if (!permissionService.canManageMembers(userId, groupId)) {
            return false;
        }
        
        // ตรวจสอบ token
        if (!validateConfirmation(confirmationToken, ConfirmationAction.REMOVE_MEMBER)) {
            return false;
        }
        
        // ใช้ token
        if (consumeToken(confirmationToken)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * ยืนยันการลบกลุ่ม
     */
    public boolean confirmGroupDeletion(Long userId, Long groupId, String confirmationToken) {
        // ตรวจสอบสิทธิ์
        if (!permissionService.canDeleteGroup(userId, groupId)) {
            return false;
        }
        
        // ตรวจสอบ token
        if (!validateConfirmation(confirmationToken, ConfirmationAction.DELETE_GROUP)) {
            return false;
        }
        
        // ใช้ token
        if (consumeToken(confirmationToken)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * ยืนยันการออกจากกลุ่ม
     */
    public boolean confirmLeaveGroup(Long userId, Long groupId, String confirmationToken) {
        // ตรวจสอบ token
        if (!validateConfirmation(confirmationToken, ConfirmationAction.LEAVE_GROUP)) {
            return false;
        }
        
        // ใช้ token
        if (consumeToken(confirmationToken)) {
            return true;
        }
        
        return false;
    }
    
    /**
     * สร้าง token ที่ปลอดภัย
     */
    private String generateSecureToken() {
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return token.toString();
    }
    
    /**
     * Enum สำหรับประเภทการกระทำที่ต้องยืนยัน
     */
    public enum ConfirmationAction {
        REMOVE_MEMBER,    // ลบสมาชิก
        DELETE_GROUP,     // ลบกลุ่ม
        LEAVE_GROUP,      // ออกจากกลุ่ม
        DELETE_CONTENT,   // ลบเนื้อหา
        REVOKE_INVITE     // เพิกถอนรหัสเชิญ
    }
    
    /**
     * Class สำหรับเก็บข้อมูลการยืนยัน
     */
    public static class ConfirmationToken {
        private final String token;
        private final Long userId;
        private final ConfirmationAction action;
        private final Map<String, Object> params;
        private final LocalDateTime expiresAt;
        private boolean used;
        
        public ConfirmationToken(String token, Long userId, ConfirmationAction action, 
                               Map<String, Object> params, LocalDateTime expiresAt, boolean used) {
            this.token = token;
            this.userId = userId;
            this.action = action;
            this.params = params;
            this.expiresAt = expiresAt;
            this.used = used;
        }
        
        // Getters
        public String getToken() { return token; }
        public Long getUserId() { return userId; }
        public ConfirmationAction getAction() { return action; }
        public Map<String, Object> getParams() { return params; }
        public LocalDateTime getExpiresAt() { return expiresAt; }
        public boolean isUsed() { return used; }
        
        // Setters
        public void setUsed(boolean used) { this.used = used; }
    }
}

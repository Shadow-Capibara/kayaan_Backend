package se499.kayaanbackend.Study_Group.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.Study_Group.GroupInvite;
import se499.kayaanbackend.Study_Group.repository.GroupInviteRepository;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service สำหรับจัดการรหัสเชิญที่ปลอดภัย
 */
@Service
public class InviteCodeService {
    
    @Autowired
    private GroupInviteRepository groupInviteRepository;
    
    private static final String ALPHABET = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int CODE_LENGTH = 8;
    private static final SecureRandom RANDOM = new SecureRandom();
    
    /**
     * สร้างรหัสเชิญที่ปลอดภัย
     */
    public String generateSecureInviteCode() {
        StringBuilder code = new StringBuilder(CODE_LENGTH);
        for (int i = 0; i < CODE_LENGTH; i++) {
            code.append(ALPHABET.charAt(RANDOM.nextInt(ALPHABET.length())));
        }
        return code.toString();
    }
    
    /**
     * สร้างรหัสเชิญที่ไม่ซ้ำกับที่มีอยู่
     */
    public String generateUniqueInviteCode() {
        String code;
        do {
            code = generateSecureInviteCode();
        } while (groupInviteRepository.findByInviteCode(code).isPresent());
        
        return code;
    }
    
    /**
     * ตรวจสอบความถูกต้องของรหัสเชิญ
     */
    public InviteValidationResult validateInviteCode(String code) {
        Optional<GroupInvite> inviteOpt = groupInviteRepository.findByInviteCode(code);
        
        if (inviteOpt.isEmpty()) {
            return new InviteValidationResult(false, "Invalid invite code", null);
        }
        
        GroupInvite invite = inviteOpt.get();
        
        // ตรวจสอบสถานะการใช้งาน
        if (!invite.getIsActive()) {
            return new InviteValidationResult(false, "Invite code is inactive", invite);
        }
        
        // ตรวจสอบการเพิกถอน
        if (invite.getRevoked()) {
            return new InviteValidationResult(false, "Invite code has been revoked", invite);
        }
        
        // ตรวจสอบวันหมดอายุ
        if (isInviteExpired(invite)) {
            return new InviteValidationResult(false, "Invite code has expired", invite);
        }
        
        // ตรวจสอบจำนวนครั้งที่ใช้
        if (isInviteUsageExceeded(invite)) {
            return new InviteValidationResult(false, "Invite code usage limit exceeded", invite);
        }
        
        return new InviteValidationResult(true, "Valid invite code", invite);
    }
    
    /**
     * ตรวจสอบวันหมดอายุ
     */
    public boolean isInviteExpired(GroupInvite invite) {
        return LocalDateTime.now().isAfter(invite.getExpiresAt());
    }
    
    /**
     * ตรวจสอบจำนวนครั้งที่ใช้
     */
    public boolean isInviteUsageExceeded(GroupInvite invite) {
        if (invite.getMaxUses() == null) {
            return false; // ไม่จำกัดจำนวนครั้ง
        }
        return invite.getCurrentUses() >= invite.getMaxUses();
    }
    
    /**
     * เพิ่มจำนวนครั้งที่ใช้รหัสเชิญ
     */
    public void incrementInviteUsage(String inviteCode) {
        Optional<GroupInvite> inviteOpt = groupInviteRepository.findByInviteCode(inviteCode);
        
        if (inviteOpt.isPresent()) {
            GroupInvite invite = inviteOpt.get();
            invite.setCurrentUses(invite.getCurrentUses() + 1);
            
            // ถ้าใช้ครบจำนวนครั้งแล้ว ให้ปิดการใช้งาน
            if (invite.getMaxUses() != null && invite.getCurrentUses() >= invite.getMaxUses()) {
                invite.setIsActive(false);
            }
            
            groupInviteRepository.save(invite);
        }
    }
    
    /**
     * เพิกถอนรหัสเชิญ
     */
    public boolean revokeInviteCode(String inviteCode, Long userId) {
        Optional<GroupInvite> inviteOpt = groupInviteRepository.findByInviteCode(inviteCode);
        
        if (inviteOpt.isPresent()) {
            GroupInvite invite = inviteOpt.get();
            invite.setRevoked(true);
            invite.setIsActive(false);
            groupInviteRepository.save(invite);
            return true;
        }
        
        return false;
    }
    
    /**
     * สร้างรหัสเชิญใหม่
     */
    public GroupInvite createInviteCode(Integer groupId, Integer createdBy, String createdByIp, 
                                      LocalDateTime expiresAt, Integer maxUses) {
        String inviteCode = generateUniqueInviteCode();
        
        GroupInvite invite = GroupInvite.builder()
                .groupId(groupId)
                .inviteCode(inviteCode)
                .createdBy(createdBy)
                .createdByIp(createdByIp)
                .expiresAt(expiresAt)
                .maxUses(maxUses)
                .currentUses(0)
                .isActive(true)
                .revoked(false)
                .createdAt(LocalDateTime.now())
                .build();
        
        return groupInviteRepository.save(invite);
    }
    
    /**
     * ดึงรายการรหัสเชิญของกลุ่ม
     */
    public List<GroupInvite> getGroupInvites(Long groupId) {
        return groupInviteRepository.findByGroupId(groupId.intValue());
    }

    /**
     * ใช้รหัสเชิญเพื่อเข้าร่วมกลุ่ม
     */
    public boolean useInviteCode(String inviteCode, Long userId) {
        Optional<GroupInvite> inviteOpt = groupInviteRepository.findByInviteCode(inviteCode);
        if (inviteOpt.isEmpty()) {
            return false;
        }

        GroupInvite invite = inviteOpt.get();
        
        // ตรวจสอบการหมดอายุ
        if (isInviteExpired(invite)) {
            return false;
        }

        // ตรวจสอบจำนวนการใช้งาน
        if (isInviteUsageExceeded(invite)) {
            return false;
        }

        // เพิ่มจำนวนการใช้งาน
        incrementInviteUsage(inviteCode);
        
        // สร้าง GroupMember ใหม่ (ต้องเพิ่ม logic นี้ใน GroupMemberService)
        // TODO: Implement group member creation
        
        return true;
    }
    
    /**
     * Class สำหรับผลลัพธ์การตรวจสอบรหัสเชิญ
     */
    public static class InviteValidationResult {
        private final boolean valid;
        private final String message;
        private final GroupInvite invite;
        
        public InviteValidationResult(boolean valid, String message, GroupInvite invite) {
            this.valid = valid;
            this.message = message;
            this.invite = invite;
        }
        
        public boolean isValid() {
            return valid;
        }
        
        public String getMessage() {
            return message;
        }
        
        public GroupInvite getInvite() {
            return invite;
        }
    }
}

package se499.kayaanbackend.Study_Group.security;

import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Service สำหรับจำกัดการใช้งานระบบ (Rate Limiting)
 */
@Service
public class RateLimitService {
    
    // เก็บข้อมูลการใช้งาน (ใน production ควรใช้ Redis)
    private final Map<String, UserActionCount> userActionCounts = new ConcurrentHashMap<>();
    
    // จำกัดการสร้างกลุ่ม: 5 กลุ่มต่อวัน
    private static final int MAX_GROUPS_PER_DAY = 5;
    
    // จำกัดการโพสต์เนื้อหา: 50 โพสต์ต่อวัน
    private static final int MAX_POSTS_PER_DAY = 50;
    
    // จำกัดการเชิญสมาชิก: 20 ครั้งต่อวัน
    private static final int MAX_INVITES_PER_DAY = 20;
    
    // จำกัดการส่งข้อความ: 100 ข้อความต่อวัน
    private static final int MAX_MESSAGES_PER_DAY = 100;
    
    /**
     * จำกัดการสร้างกลุ่ม
     */
    public boolean canCreateGroup(Long userId) {
        String key = "create_group:" + userId;
        return checkRateLimit(key, MAX_GROUPS_PER_DAY);
    }
    
    /**
     * จำกัดการโพสต์เนื้อหา
     */
    public boolean canPostContent(Long userId, Long groupId) {
        String key = "post_content:" + userId + ":" + groupId;
        return checkRateLimit(key, MAX_POSTS_PER_DAY);
    }
    
    /**
     * จำกัดการเชิญสมาชิก
     */
    public boolean canInviteMembers(Long userId, Long groupId) {
        String key = "invite_members:" + userId + ":" + groupId;
        return checkRateLimit(key, MAX_INVITES_PER_DAY);
    }
    
    /**
     * จำกัดการส่งข้อความ
     */
    public boolean canSendMessage(Long userId, Long groupId) {
        String key = "send_message:" + userId + ":" + groupId;
        return checkRateLimit(key, MAX_MESSAGES_PER_DAY);
    }
    
    /**
     * จำกัดการอัปโหลดไฟล์
     */
    public boolean canUploadFile(Long userId, Long groupId) {
        String key = "upload_file:" + userId + ":" + groupId;
        return checkRateLimit(key, MAX_POSTS_PER_DAY); // ใช้ limit เดียวกับโพสต์เนื้อหา
    }
    
    /**
     * จำกัดการสร้างรหัสเชิญ
     */
    public boolean canCreateInvite(Long userId, Long groupId) {
        String key = "create_invite:" + userId + ":" + groupId;
        return checkRateLimit(key, MAX_INVITES_PER_DAY);
    }
    
    /**
     * จำกัดการลบเนื้อหา
     */
    public boolean canDeleteContent(Long userId, Long groupId) {
        String key = "delete_content:" + userId + ":" + groupId;
        return checkRateLimit(key, 20); // จำกัดการลบ 20 ครั้งต่อวัน
    }
    
    /**
     * จำกัดการแก้ไขเนื้อหา
     */
    public boolean canEditContent(Long userId, Long groupId) {
        String key = "edit_content:" + userId + ":" + groupId;
        return checkRateLimit(key, 30); // จำกัดการแก้ไข 30 ครั้งต่อวัน
    }
    
    /**
     * ตรวจสอบ rate limit
     */
    private boolean checkRateLimit(String key, int maxActions) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        
        UserActionCount count = userActionCounts.get(key);
        
        if (count == null) {
            // สร้างใหม่
            count = new UserActionCount(key, startOfDay, 1);
            userActionCounts.put(key, count);
            return true;
        }
        
        // ตรวจสอบว่าต้อง reset หรือไม่
        if (count.getStartTime().isBefore(startOfDay)) {
            // reset สำหรับวันใหม่
            count.setStartTime(startOfDay);
            count.setCount(1);
            return true;
        }
        
        // ตรวจสอบจำนวนครั้ง
        if (count.getCount() >= maxActions) {
            return false;
        }
        
        // เพิ่มจำนวนครั้ง
        count.incrementCount();
        return true;
    }
    
    /**
     * รับข้อมูล rate limit ของผู้ใช้
     */
    public RateLimitInfo getRateLimitInfo(Long userId, Long groupId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime startOfDay = now.toLocalDate().atStartOfDay();
        
        return RateLimitInfo.builder()
                .canCreateGroup(canCreateGroup(userId))
                .canPostContent(canPostContent(userId, groupId))
                .canInviteMembers(canInviteMembers(userId, groupId))
                .canSendMessage(canSendMessage(userId, groupId))
                .canUploadFile(canUploadFile(userId, groupId))
                .canCreateInvite(canCreateInvite(userId, groupId))
                .canDeleteContent(canDeleteContent(userId, groupId))
                .canEditContent(canEditContent(userId, groupId))
                .resetTime(startOfDay.plusDays(1))
                .build();
    }
    
    /**
     * ล้างข้อมูล rate limit (สำหรับ testing หรือ admin)
     */
    public void clearRateLimit(String key) {
        userActionCounts.remove(key);
    }
    
    /**
     * ล้างข้อมูล rate limit ทั้งหมด (สำหรับ testing หรือ admin)
     */
    public void clearAllRateLimits() {
        userActionCounts.clear();
    }
    
    /**
     * Class สำหรับเก็บจำนวนการกระทำของผู้ใช้
     */
    private static class UserActionCount {
        private final String key;
        private LocalDateTime startTime;
        private int count;
        
        public UserActionCount(String key, LocalDateTime startTime, int count) {
            this.key = key;
            this.startTime = startTime;
            this.count = count;
        }
        
        public LocalDateTime getStartTime() { return startTime; }
        public void setStartTime(LocalDateTime startTime) { this.startTime = startTime; }
        public int getCount() { return count; }
        public void setCount(int count) { this.count = count; }
        public void incrementCount() { this.count++; }
    }
    
    /**
     * Class สำหรับข้อมูล rate limit
     */
    public static class RateLimitInfo {
        private final boolean canCreateGroup;
        private final boolean canPostContent;
        private final boolean canInviteMembers;
        private final boolean canSendMessage;
        private final boolean canUploadFile;
        private final boolean canCreateInvite;
        private final boolean canDeleteContent;
        private final boolean canEditContent;
        private final LocalDateTime resetTime;
        
        private RateLimitInfo(Builder builder) {
            this.canCreateGroup = builder.canCreateGroup;
            this.canPostContent = builder.canPostContent;
            this.canInviteMembers = builder.canInviteMembers;
            this.canSendMessage = builder.canSendMessage;
            this.canUploadFile = builder.canUploadFile;
            this.canCreateInvite = builder.canCreateInvite;
            this.canDeleteContent = builder.canDeleteContent;
            this.canEditContent = builder.canEditContent;
            this.resetTime = builder.resetTime;
        }
        
        // Getters
        public boolean canCreateGroup() { return canCreateGroup; }
        public boolean canPostContent() { return canPostContent; }
        public boolean canInviteMembers() { return canInviteMembers; }
        public boolean canSendMessage() { return canSendMessage; }
        public boolean canUploadFile() { return canUploadFile; }
        public boolean canCreateInvite() { return canCreateInvite; }
        public boolean canDeleteContent() { return canDeleteContent; }
        public boolean canEditContent() { return canEditContent; }
        public LocalDateTime getResetTime() { return resetTime; }
        
        // Builder
        public static class Builder {
            private boolean canCreateGroup;
            private boolean canPostContent;
            private boolean canInviteMembers;
            private boolean canSendMessage;
            private boolean canUploadFile;
            private boolean canCreateInvite;
            private boolean canDeleteContent;
            private boolean canEditContent;
            private LocalDateTime resetTime;
            
            public Builder canCreateGroup(boolean canCreateGroup) {
                this.canCreateGroup = canCreateGroup;
                return this;
            }
            
            public Builder canPostContent(boolean canPostContent) {
                this.canPostContent = canPostContent;
                return this;
            }
            
            public Builder canInviteMembers(boolean canInviteMembers) {
                this.canInviteMembers = canInviteMembers;
                return this;
            }
            
            public Builder canSendMessage(boolean canSendMessage) {
                this.canSendMessage = canSendMessage;
                return this;
            }
            
            public Builder canUploadFile(boolean canUploadFile) {
                this.canUploadFile = canUploadFile;
                return this;
            }
            
            public Builder canCreateInvite(boolean canCreateInvite) {
                this.canCreateInvite = canCreateInvite;
                return this;
            }
            
            public Builder canDeleteContent(boolean canDeleteContent) {
                this.canDeleteContent = canDeleteContent;
                return this;
            }
            
            public Builder canEditContent(boolean canEditContent) {
                this.canEditContent = canEditContent;
                return this;
            }
            
            public Builder resetTime(LocalDateTime resetTime) {
                this.resetTime = resetTime;
                return this;
            }
            
            public RateLimitInfo build() {
                return new RateLimitInfo(this);
            }
        }
        
        public static Builder builder() {
            return new Builder();
        }
    }
}

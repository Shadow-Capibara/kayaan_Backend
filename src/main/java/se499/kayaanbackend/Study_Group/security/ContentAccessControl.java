package se499.kayaanbackend.Study_Group.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import se499.kayaanbackend.Study_Group.GroupContent;
import se499.kayaanbackend.Study_Group.repository.GroupContentRepository;

import java.util.Optional;

/**
 * Component สำหรับควบคุมการเข้าถึงเนื้อหาในกลุ่มเรียน
 */
@Component
public class ContentAccessControl {
    
    @Autowired
    private GroupPermissionService permissionService;
    
    @Autowired
    private GroupContentRepository groupContentRepository;
    
    /**
     * ตรวจสอบสิทธิ์การดูเนื้อหา
     */
    public boolean canViewContent(Long userId, Long contentId) {
        Optional<GroupContent> contentOpt = groupContentRepository.findById(contentId);
        
        if (contentOpt.isEmpty()) {
            return false;
        }
        
        GroupContent content = contentOpt.get();
        return permissionService.hasPermission(userId, content.getGroupId().longValue(), GroupPermission.VIEW_GROUP);
    }
    
    /**
     * ตรวจสอบสิทธิ์การแก้ไขเนื้อหา
     */
    public boolean canEditContent(Long userId, Long contentId) {
        Optional<GroupContent> contentOpt = groupContentRepository.findById(contentId);
        
        if (contentOpt.isEmpty()) {
            return false;
        }
        
        GroupContent content = contentOpt.get();
        
        // ถ้าเป็นเจ้าของเนื้อหา สามารถแก้ไขได้
        if (content.getUploaderId().equals(userId.intValue())) {
            return permissionService.hasPermission(userId, content.getGroupId().longValue(), GroupPermission.EDIT_OWN_CONTENT);
        }
        
        // ถ้าไม่ใช่เจ้าของ ต้องมีสิทธิ์แก้ไขเนื้อหาใดๆ
        return permissionService.hasPermission(userId, content.getGroupId().longValue(), GroupPermission.EDIT_ANY_CONTENT);
    }
    
    /**
     * ตรวจสอบสิทธิ์การลบเนื้อหา
     */
    public boolean canDeleteContent(Long userId, Long contentId) {
        Optional<GroupContent> contentOpt = groupContentRepository.findById(contentId);
        
        if (contentOpt.isEmpty()) {
            return false;
        }
        
        GroupContent content = contentOpt.get();
        
        // ถ้าเป็นเจ้าของเนื้อหา สามารถลบได้
        if (content.getUploaderId().equals(userId.intValue())) {
            return permissionService.hasPermission(userId, content.getGroupId().longValue(), GroupPermission.DELETE_OWN_CONTENT);
        }
        
        // ถ้าไม่ใช่เจ้าของ ต้องมีสิทธิ์ลบเนื้อหาใดๆ
        return permissionService.hasPermission(userId, content.getGroupId().longValue(), GroupPermission.DELETE_ANY_CONTENT);
    }
    
    /**
     * ตรวจสอบสิทธิ์การอัปโหลดไฟล์
     */
    public boolean canUploadFile(Long userId, Long groupId) {
        return permissionService.hasPermission(userId, groupId, GroupPermission.POST_CONTENT);
    }
    
    /**
     * ตรวจสอบสิทธิ์การอัปเดตเนื้อหา
     */
    public boolean canUpdateContent(Long userId, Long contentId) {
        return canEditContent(userId, contentId);
    }
    
    /**
     * ตรวจสอบสิทธิ์การดูรายการเนื้อหา
     */
    public boolean canListContent(Long userId, Long groupId) {
        return permissionService.hasPermission(userId, groupId, GroupPermission.VIEW_GROUP);
    }
    
    /**
     * ตรวจสอบสิทธิ์การค้นหาเนื้อหา
     */
    public boolean canSearchContent(Long userId, Long groupId) {
        return permissionService.hasPermission(userId, groupId, GroupPermission.VIEW_GROUP);
    }
}

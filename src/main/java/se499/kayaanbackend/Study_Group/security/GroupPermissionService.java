package se499.kayaanbackend.Study_Group.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.Study_Group.GroupMember;
import se499.kayaanbackend.Study_Group.GroupContent;
import se499.kayaanbackend.Study_Group.StudyGroup;
import se499.kayaanbackend.Study_Group.repository.GroupMemberRepository;
import se499.kayaanbackend.Study_Group.repository.GroupContentRepository;
import se499.kayaanbackend.Study_Group.repository.StudyGroupRepository;
import se499.kayaanbackend.Study_Group.security.GroupPermission;
import se499.kayaanbackend.Study_Group.security.GroupRole;
import se499.kayaanbackend.Study_Group.security.PermissionIntegrationService;

import java.util.Optional;
import java.util.Set;

/**
 * Service สำหรับจัดการสิทธิ์และตรวจสอบการเข้าถึงในกลุ่มเรียน
 */
@Service
public class GroupPermissionService {
    
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    
    @Autowired
    private GroupContentRepository groupContentRepository;
    
    @Autowired
    private StudyGroupRepository studyGroupRepository;
    
    @Autowired
    private PermissionIntegrationService permissionIntegrationService;

    /**
     * ตรวจสอบว่าผู้ใช้มีสิทธิ์ทำอะไรในกลุ่ม
     */
    public boolean hasPermission(Long userId, Long groupId, GroupPermission permission) {
        Optional<GroupMember> memberOpt = groupMemberRepository.findByGroupIdAndUserId(groupId.intValue(), userId.intValue());
        
        if (memberOpt.isEmpty()) {
            return false;
        }
        
        GroupMember member = memberOpt.get();
        // Convert GroupMember.Role to GroupRole
        GroupRole role;
        if (member.getRole() == GroupMember.Role.admin) {
            role = GroupRole.ADMIN;
        } else {
            role = GroupRole.MEMBER;
        }
        
        return role.hasPermission(permission);
    }
    
    /**
     * ตรวจสอบบทบาทของผู้ใช้ในกลุ่ม
     */
    public GroupRole getUserRole(Long groupId, Long userId) {
        Optional<GroupMember> memberOpt = groupMemberRepository.findByGroupIdAndUserId(groupId.intValue(), userId.intValue());
        
        if (memberOpt.isEmpty()) {
            return GroupRole.MEMBER; // Default role
        }

        GroupMember member = memberOpt.get();
        // Convert GroupMember.Role to GroupRole
        if (member.getRole() == GroupMember.Role.admin) {
            return GroupRole.ADMIN;
        } else {
            return GroupRole.MEMBER;
        }
    }
    
    /**
     * ตรวจสอบสิทธิ์การเข้าถึงเนื้อหา
     */
    public boolean canAccessContent(Long userId, Long contentId) {
        Optional<GroupContent> contentOpt = groupContentRepository.findById(contentId);
        
        if (contentOpt.isEmpty()) {
            return false;
        }
        
        GroupContent content = contentOpt.get();
        return hasPermission(userId, content.getGroupId().longValue(), GroupPermission.VIEW_GROUP);
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
            return hasPermission(userId, content.getGroupId().longValue(), GroupPermission.EDIT_OWN_CONTENT);
        }
        
        // ถ้าไม่ใช่เจ้าของ ต้องมีสิทธิ์แก้ไขเนื้อหาใดๆ
        return hasPermission(userId, content.getGroupId().longValue(), GroupPermission.EDIT_ANY_CONTENT);
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
            return hasPermission(userId, content.getGroupId().longValue(), GroupPermission.DELETE_OWN_CONTENT);
        }
        
        // ถ้าไม่ใช่เจ้าของ ต้องมีสิทธิ์ลบเนื้อหาใดๆ
        return hasPermission(userId, content.getGroupId().longValue(), GroupPermission.DELETE_ANY_CONTENT);
    }
    
    /**
     * ตรวจสอบสิทธิ์การจัดการสมาชิก
     */
    public boolean canManageMembers(Long userId, Long groupId) {
        return hasPermission(userId, groupId, GroupPermission.MANAGE_MEMBERS);
    }
    
    /**
     * ตรวจสอบสิทธิ์การลบกลุ่ม
     */
    public boolean canDeleteGroup(Long userId, Long groupId) {
        return hasPermission(userId, groupId, GroupPermission.DELETE_GROUP);
    }
    
    /**
     * ตรวจสอบสิทธิ์การเชิญสมาชิก
     */
    public boolean canInviteMembers(Long userId, Long groupId) {
        return hasPermission(userId, groupId, GroupPermission.INVITE_MEMBERS);
    }
    
    /**
     * ตรวจสอบว่าผู้ใช้เป็นเจ้าของกลุ่มหรือไม่
     */
    public boolean isGroupOwner(Long userId, Long groupId) {
        Optional<StudyGroup> groupOpt = studyGroupRepository.findById(groupId.intValue());
        
        if (groupOpt.isEmpty()) {
            return false;
        }
        
        StudyGroup group = groupOpt.get();
        return group.getOwner().getId().equals(userId);
    }
    
    /**
     * ตรวจสอบว่าผู้ใช้เป็นสมาชิกในกลุ่มหรือไม่
     */
    public boolean isGroupMember(Long userId, Long groupId) {
        return groupMemberRepository.findByGroupIdAndUserId(groupId.intValue(), userId.intValue()).isPresent();
    }

    /**
     * ตรวจสอบสิทธิ์แบบ hierarchical
     */
    public boolean hasHierarchicalPermission(Long groupId, Long userId, GroupPermission permission) {
        return permissionIntegrationService.hasHierarchicalPermission(groupId, userId, permission);
    }

    /**
     * ดึงสิทธิ์ทั้งหมดของผู้ใช้
     */
    public Set<GroupPermission> getUserPermissions(Long groupId, Long userId) {
        return permissionIntegrationService.getUserPermissions(groupId, userId);
    }
}

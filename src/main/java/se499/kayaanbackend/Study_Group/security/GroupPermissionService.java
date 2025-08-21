package se499.kayaanbackend.Study_Group.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.Study_Group.GroupMember;
import se499.kayaanbackend.Study_Group.GroupContent;
import se499.kayaanbackend.Study_Group.StudyGroup;
import se499.kayaanbackend.Study_Group.repository.GroupMemberRepository;
import se499.kayaanbackend.Study_Group.repository.GroupContentRepository;
import se499.kayaanbackend.Study_Group.repository.StudyGroupRepository;

import java.util.Optional;

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
    
    /**
     * ตรวจสอบว่าผู้ใช้มีสิทธิ์ทำอะไรในกลุ่ม
     */
    public boolean hasPermission(Long userId, Long groupId, GroupPermission permission) {
        Optional<GroupMember> memberOpt = groupMemberRepository.findByUserIdAndGroupId(userId, groupId);
        
        if (memberOpt.isEmpty()) {
            return false;
        }
        
        GroupMember member = memberOpt.get();
        GroupRole role = GroupRole.valueOf(member.getRole());
        
        return role.hasPermission(permission);
    }
    
    /**
     * ตรวจสอบบทบาทของผู้ใช้ในกลุ่ม
     */
    public GroupRole getUserRole(Long userId, Long groupId) {
        Optional<GroupMember> memberOpt = groupMemberRepository.findByUserIdAndGroupId(userId, groupId);
        
        if (memberOpt.isEmpty()) {
            return null;
        }
        
        GroupMember member = memberOpt.get();
        return GroupRole.valueOf(member.getRole());
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
        return hasPermission(userId, content.getGroupId(), GroupPermission.VIEW_GROUP);
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
        if (content.getUserId().equals(userId)) {
            return hasPermission(userId, content.getGroupId(), GroupPermission.EDIT_OWN_CONTENT);
        }
        
        // ถ้าไม่ใช่เจ้าของ ต้องมีสิทธิ์แก้ไขเนื้อหาใดๆ
        return hasPermission(userId, content.getGroupId(), GroupPermission.EDIT_ANY_CONTENT);
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
        if (content.getUserId().equals(userId)) {
            return hasPermission(userId, content.getGroupId(), GroupPermission.DELETE_OWN_CONTENT);
        }
        
        // ถ้าไม่ใช่เจ้าของ ต้องมีสิทธิ์ลบเนื้อหาใดๆ
        return hasPermission(userId, content.getGroupId(), GroupPermission.DELETE_ANY_CONTENT);
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
        Optional<StudyGroup> groupOpt = studyGroupRepository.findById(groupId);
        
        if (groupOpt.isEmpty()) {
            return false;
        }
        
        StudyGroup group = groupOpt.get();
        return group.getOwnerId().equals(userId);
    }
    
    /**
     * ตรวจสอบว่าผู้ใช้เป็นสมาชิกในกลุ่มหรือไม่
     */
    public boolean isGroupMember(Long userId, Long groupId) {
        return groupMemberRepository.findByUserIdAndGroupId(userId, groupId).isPresent();
    }
}

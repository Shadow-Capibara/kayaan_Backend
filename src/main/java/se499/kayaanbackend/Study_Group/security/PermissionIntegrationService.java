package se499.kayaanbackend.Study_Group.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.Study_Group.GroupMember;
import se499.kayaanbackend.Study_Group.repository.GroupMemberRepository;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

/**
 * Service สำหรับจัดการสิทธิ์แบบ hierarchical และ integration
 */
@Service
public class PermissionIntegrationService {
    
    @Autowired
    private GroupMemberRepository groupMemberRepository;
    
    /**
     * แปลง GroupMember.Role เป็น GroupRole
     */
    private GroupRole mapToGroupRole(GroupMember.Role memberRole) {
        switch (memberRole) {
            case admin:
                return GroupRole.ADMIN;
            case member:
            default:
                return GroupRole.MEMBER;
        }
    }
    
    /**
     * ตรวจสอบสิทธิ์แบบ hierarchical
     */
    public boolean hasHierarchicalPermission(Long groupId, Long userId, GroupPermission permission) {
        Optional<GroupMember> memberOpt = groupMemberRepository.findByGroupIdAndUserId(
            groupId.intValue(), userId.intValue());
        
        if (memberOpt.isEmpty()) {
            return false;
        }
        
        GroupMember member = memberOpt.get();
        GroupRole role = mapToGroupRole(member.getRole());
        
        return role.hasPermission(permission);
    }
    
    /**
     * ดึงสิทธิ์ทั้งหมดของผู้ใช้
     */
    public Set<GroupPermission> getUserPermissions(Long groupId, Long userId) {
        Optional<GroupMember> memberOpt = groupMemberRepository.findByGroupIdAndUserId(
            groupId.intValue(), userId.intValue());
        
        if (memberOpt.isEmpty()) {
            return new HashSet<>();
        }
        
        GroupMember member = memberOpt.get();
        GroupRole role = mapToGroupRole(member.getRole());
        
        return new HashSet<>(Arrays.asList(role.getPermissions()));
    }
    
    /**
     * ตรวจสอบสิทธิ์แบบ advanced
     */
    public boolean hasAdvancedPermission(Long groupId, Long userId, GroupPermission permission, Object context) {
        // Basic permission check
        if (!hasHierarchicalPermission(groupId, userId, permission)) {
            return false;
        }
        
        // Context-specific checks can be added here
        // For example, checking if user owns the content they're trying to modify
        
        return true;
    }
    
    /**
     * ดึงบทบาทของผู้ใช้
     */
    public GroupRole getUserRole(Long groupId, Long userId) {
        Optional<GroupMember> memberOpt = groupMemberRepository.findByGroupIdAndUserId(
            groupId.intValue(), userId.intValue());
        
        if (memberOpt.isEmpty()) {
            return null;
        }
        
        GroupMember member = memberOpt.get();
        return mapToGroupRole(member.getRole());
    }
    
    /**
     * ตรวจสอบว่าผู้ใช้มีสิทธิ์ในหลายกลุ่ม
     */
    public boolean hasPermissionInMultipleGroups(Long userId, GroupPermission permission, Long... groupIds) {
        for (Long groupId : groupIds) {
            if (!hasHierarchicalPermission(groupId, userId, permission)) {
                return false;
            }
        }
        return true;
    }
}

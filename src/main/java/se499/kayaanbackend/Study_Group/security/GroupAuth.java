package se499.kayaanbackend.Study_Group.security;

import java.util.Optional;

import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import se499.kayaanbackend.Study_Group.GroupMember;
import se499.kayaanbackend.Study_Group.repository.GroupMemberRepository;

@Component
@RequiredArgsConstructor
public class GroupAuth {
    
    private final GroupMemberRepository groupMemberRepository;
    
    /**
     * Check if user is the owner of the group
     */
    public boolean isOwner(Integer userId, Integer groupId) {
        Optional<GroupMember> member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        return member.isPresent() && member.get().getRole() == GroupMember.Role.admin;
    }
    
    /**
     * Check if user is an admin of the group
     */
    public boolean isAdmin(Integer userId, Integer groupId) {
        Optional<GroupMember> member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        return member.isPresent() && member.get().getRole() == GroupMember.Role.admin;
    }
    
    /**
     * Check if user is a member of the group
     */
    public boolean isMember(Integer userId, Integer groupId) {
        return groupMemberRepository.existsByGroupIdAndUserId(groupId, userId);
    }
    
    /**
     * Check if user has a specific role in the group
     */
    public boolean hasRole(Integer userId, Integer groupId, GroupMember.Role role) {
        Optional<GroupMember> member = groupMemberRepository.findByGroupIdAndUserId(groupId, userId);
        return member.isPresent() && member.get().getRole() == role;
    }
}

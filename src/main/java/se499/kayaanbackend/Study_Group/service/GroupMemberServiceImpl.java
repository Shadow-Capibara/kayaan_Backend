package se499.kayaanbackend.Study_Group.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import se499.kayaanbackend.Study_Group.GroupMember;
import se499.kayaanbackend.Study_Group.dto.MemberResponse;
import se499.kayaanbackend.Study_Group.dto.UpdateMemberRoleRequest;
import se499.kayaanbackend.Study_Group.repository.GroupMemberRepository;
import se499.kayaanbackend.Study_Group.repository.StudyGroupRepository;
import se499.kayaanbackend.security.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupMemberServiceImpl implements GroupMemberService {
    
    private final GroupMemberRepository groupMemberRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final UserRepository userRepository;
    
    @Override
    public List<MemberResponse> getMembers(Integer currentUserId, Integer groupId) {
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new RuntimeException("Access denied: User is not a member of this group");
        }
        
        List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
        
        return members.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public MemberResponse updateRole(Integer currentUserId, Integer groupId, Integer userId, UpdateMemberRoleRequest request) {
        // Check if current user is a member
        GroupMember currentMember = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new RuntimeException("Access denied: User is not a member of this group"));
        
        // Check if target user is a member
        GroupMember targetMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("Target user is not a member of this group"));
        
        // Check permissions
        if (currentMember.getRole() == GroupMember.Role.member) {
            throw new RuntimeException("Access denied: Only owners and moderators can update roles");
        }
        
        if (currentMember.getRole() == GroupMember.Role.admin) {
            // Admins can only promote to admin, not to owner
            if (request.role() == GroupMember.Role.admin) {
                throw new RuntimeException("Access denied: Admins cannot promote to admin");
            }
            // Admins cannot demote other admins
            if (targetMember.getRole() == GroupMember.Role.admin) {
                throw new RuntimeException("Access denied: Admins cannot demote other admins");
            }
        }
        
        // Admins cannot be demoted
        if (targetMember.getRole() == GroupMember.Role.admin && request.role() != GroupMember.Role.admin) {
            throw new RuntimeException("Access denied: Admins cannot be demoted");
        }
        
        targetMember.setRole(request.role());
        GroupMember updatedMember = groupMemberRepository.save(targetMember);
        
        return mapToResponse(updatedMember);
    }
    
    @Override
    public void removeMember(Integer currentUserId, Integer groupId, Integer userId) {
        // Check if current user is a member
        GroupMember currentMember = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new RuntimeException("Access denied: User is not a member of this group"));
        
        // Check if target user is a member
        GroupMember targetMember = groupMemberRepository.findByGroupIdAndUserId(groupId, userId)
                .orElseThrow(() -> new RuntimeException("Target user is not a member of this group"));
        
        // Check permissions
        if (currentMember.getRole() == GroupMember.Role.member) {
            throw new RuntimeException("Access denied: Only owners and moderators can remove members");
        }
        
        if (currentMember.getRole() == GroupMember.Role.admin) {
            // Admins cannot remove other admins
            if (targetMember.getRole() == GroupMember.Role.admin) {
                throw new RuntimeException("Access denied: Admins cannot remove other admins");
            }
        }
        
        // Owners cannot remove themselves
        if (currentUserId.equals(userId)) {
            throw new RuntimeException("Access denied: Users cannot remove themselves");
        }
        
        groupMemberRepository.delete(targetMember);
    }
    
    @Override
    public void inviteByEmail(Integer currentUserId, Integer groupId, String email) {
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new RuntimeException("Access denied: User is not a member of this group");
        }
        
        // TODO: Implement email sending logic
        // For now, this is a stub implementation
        throw new RuntimeException("Email invitation not yet implemented");
    }
    
    private MemberResponse mapToResponse(GroupMember member) {
        return new MemberResponse(
                member.getUserId(),
                member.getRole(),
                member.getJoinedAt()
        );
    }
}

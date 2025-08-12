package se499.kayaanbackend.Study_Group.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import se499.kayaanbackend.Study_Group.GroupInvite;
import se499.kayaanbackend.Study_Group.GroupMember;
import se499.kayaanbackend.Study_Group.StudyGroup;
import se499.kayaanbackend.Study_Group.dto.CreateGroupRequest;
import se499.kayaanbackend.Study_Group.dto.InviteResponse;
import se499.kayaanbackend.Study_Group.dto.StudyGroupResponse;
import se499.kayaanbackend.Study_Group.repository.GroupInviteRepository;
import se499.kayaanbackend.Study_Group.repository.GroupMemberRepository;
import se499.kayaanbackend.Study_Group.repository.StudyGroupRepository;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.security.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class StudyGroupServiceImpl implements StudyGroupService {
    
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final GroupInviteRepository groupInviteRepository;
    private final UserRepository userRepository;
    
    @Override
    @Transactional
    public StudyGroupResponse createGroup(Integer currentUserId, CreateGroupRequest request) {
        User owner = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        StudyGroup group = StudyGroup.builder()
                .name(request.name())
                .description(request.description())
                .owner(owner)
                .build();
        
        StudyGroup savedGroup = studyGroupRepository.save(group);
        
        // Add owner as member with admin role
        GroupMember ownerMember = GroupMember.builder()
                .groupId(savedGroup.getId())
                .userId(currentUserId)
                .role(GroupMember.Role.admin)
                .build();
        
        groupMemberRepository.save(ownerMember);
        
        return mapToResponse(savedGroup);
    }
    
    @Override
    public List<StudyGroupResponse> getMyGroups(Integer currentUserId) {
        List<GroupMember> memberships = groupMemberRepository.findByUserId(currentUserId);
        
        return memberships.stream()
                .map(membership -> {
                    StudyGroup group = studyGroupRepository.findById(membership.getGroupId())
                            .orElse(null);
                    return group != null ? mapToResponse(group) : null;
                })
                .filter(response -> response != null)
                .collect(Collectors.toList());
    }
    
    @Override
    public StudyGroupResponse getGroup(Integer currentUserId, Integer groupId) {
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new RuntimeException("Access denied: User is not a member of this group");
        }
        
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        return mapToResponse(group);
    }
    
    @Override
    public StudyGroupResponse joinByToken(Integer currentUserId, String token) {
        GroupInvite invite = groupInviteRepository.findValidByToken(token, LocalDateTime.now())
                .orElseThrow(() -> new RuntimeException("Invalid or expired invite token"));
        
        // Check if user is already a member
        if (groupMemberRepository.existsByGroupIdAndUserId(invite.getGroupId(), currentUserId)) {
            throw new RuntimeException("User is already a member of this group");
        }
        
        // Add user as member
        GroupMember member = GroupMember.builder()
                .groupId(invite.getGroupId())
                .userId(currentUserId)
                .role(GroupMember.Role.member)
                .joinedAt(LocalDateTime.now())
                .build();
        
        groupMemberRepository.save(member);
        
        StudyGroup group = studyGroupRepository.findById(invite.getGroupId())
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        return mapToResponse(group);
    }
    
    @Override
    public void leaveGroup(Integer currentUserId, Integer groupId) {
        GroupMember membership = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        
        // Check if user is the admin
        if (membership.getRole() == GroupMember.Role.admin) {
            // Check if owner is the only member
            List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
            if (members.size() == 1) {
                throw new RuntimeException("Owner cannot leave the group if they are the only member");
            }
        }
        
        groupMemberRepository.delete(membership);
    }
    
    @Override
    public void deleteGroup(Integer currentUserId, Integer groupId) {
        GroupMember membership = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        
        if (membership.getRole() != GroupMember.Role.admin) {
            throw new RuntimeException("Only the admin can delete the group");
        }
        
        studyGroupRepository.deleteById(groupId);
    }
    
    @Override
    public InviteResponse generateInvite(Integer currentUserId, Integer groupId) {
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new RuntimeException("Access denied: User is not a member of this group");
        }
        
        String token = UUID.randomUUID().toString();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(7); // 7 days expiry
        
        GroupInvite invite = GroupInvite.builder()
                .groupId(groupId)
                .token(token)
                .expiresAt(expiresAt)
                .createdBy(currentUserId)
                .createdAt(LocalDateTime.now())
                .revoked(false)
                .build();
        
        groupInviteRepository.save(invite);
        
        return new InviteResponse(token, expiresAt);
    }
    
    private StudyGroupResponse mapToResponse(StudyGroup group) {
        return new StudyGroupResponse(
                group.getId(),
                group.getName(),
                group.getDescription(),
                group.getOwner() != null ? group.getOwner().getId() : null,
                group.getCreatedAt()
        );
    }
}

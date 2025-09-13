package se499.kayaanbackend.Study_Group.service;

import java.time.LocalDateTime;
import java.util.List;
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
import se499.kayaanbackend.Study_Group.exception.StudyGroupException;
import se499.kayaanbackend.Study_Group.repository.GroupContentRepository;
import se499.kayaanbackend.Study_Group.repository.GroupInviteRepository;
import se499.kayaanbackend.Study_Group.repository.GroupMemberRepository;
import se499.kayaanbackend.Study_Group.repository.GroupMessageRepository;
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
    private final GroupContentRepository groupContentRepository;
    private final GroupMessageRepository groupMessageRepository;
    private final UserRepository userRepository;
    private final GroupNotificationService notificationService;
    
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
        
        // Generate invite token automatically for new group
        try {
            generateInvite(currentUserId, savedGroup.getId(), 30); // 30 days expiry
        } catch (Exception e) {
            // Log error but don't fail group creation
            System.err.println("Failed to generate invite token for group " + savedGroup.getId() + ": " + e.getMessage());
        }
        
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
        // Try to find invite by token or invite code
        GroupInvite invite = groupInviteRepository.findValidByToken(token, LocalDateTime.now())
                .orElseGet(() -> groupInviteRepository.findByInviteCodeAndIsActiveTrue(token)
                        .orElseThrow(() -> new StudyGroupException("Invalid or expired invite token", 400)));
        
        // Check if user is already a member
        if (groupMemberRepository.existsByGroupIdAndUserId(invite.getGroupId(), currentUserId)) {
            throw new StudyGroupException("User is already a member of this group", 400);
        }
        
        // Check if user has a pending invitation (only if they created it themselves)
        // This check is not needed for joining via invite code - users should be able to join
        // groups they didn't create using valid invite codes
        // if (groupInviteRepository.existsByGroupIdAndCreatedByAndRevokedFalse(invite.getGroupId(), currentUserId)) {
        //     throw new RuntimeException("User already has a pending invitation to this group");
        // }
        
        // Add user as member
        GroupMember member = GroupMember.builder()
                .groupId(invite.getGroupId())
                .userId(currentUserId)
                .role(GroupMember.Role.member)
                .joinedAt(LocalDateTime.now())
                .build();
        
        groupMemberRepository.save(member);
        
        // Notify about new member joining
        notificationService.notifyMemberJoined(invite.getGroupId(), currentUserId);
        
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
        
        // Notify about member leaving
        notificationService.notifyMemberLeft(groupId, currentUserId);
    }
    
    @Override
    public void deleteGroup(Integer currentUserId, Integer groupId) {
        GroupMember membership = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new RuntimeException("User is not a member of this group"));
        
        if (membership.getRole() != GroupMember.Role.admin) {
            throw new RuntimeException("Only the admin can delete the group");
        }
        
        // Delete related data first (cascade delete)
        // 1. Delete all group messages
        groupMessageRepository.deleteByStudyGroupId(groupId);
        
        // 2. Delete all group content/resources
        groupContentRepository.deleteByGroupId(groupId);
        
        // 3. Delete all group members
        groupMemberRepository.deleteByGroupId(groupId);
        
        // 4. Delete all group invites
        groupInviteRepository.deleteByGroupId(groupId);
        
        // 5. Finally delete the group
        studyGroupRepository.deleteById(groupId);
    }
    
    @Override
    public InviteResponse generateInvite(Integer currentUserId, Integer groupId, int expiryDays) {
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new RuntimeException("Access denied: User is not a member of this group");
        }
        
        // Generate short, memorable invite code (6 characters)
        String inviteCode = generateShortInviteCode();
        LocalDateTime expiresAt = LocalDateTime.now().plusDays(expiryDays);
        
        GroupInvite invite = GroupInvite.builder()
                .groupId(groupId)
                .token(inviteCode) // Use invite code as token
                .inviteCode(inviteCode)
                .expiresAt(expiresAt)
                .createdBy(currentUserId)
                .createdAt(LocalDateTime.now())
                .revoked(false)
                .isActive(true)
                .build();
        
        groupInviteRepository.save(invite);
        
        return new InviteResponse(inviteCode, expiresAt, null, 0);
    }
    
    /**
     * Generate short, memorable invite code (6 characters)
     */
    private String generateShortInviteCode() {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt((int) (Math.random() * chars.length())));
        }
        return code.toString();
    }
    
    @Override
    public InviteResponse validateInviteToken(String token) {
        // Try to find invite by token or invite code
        GroupInvite invite = groupInviteRepository.findValidByToken(token, LocalDateTime.now())
                .orElseGet(() -> groupInviteRepository.findByInviteCodeAndIsActiveTrue(token)
                        .orElseThrow(() -> new RuntimeException("Invalid or expired invite token")));
        
        return new InviteResponse(token, invite.getExpiresAt(), invite.getMaxUses(), invite.getCurrentUses());
    }
    
    @Override
    public InviteResponse getGroupInviteCode(Integer currentUserId, Integer groupId) {
        // Check if user is a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new RuntimeException("Access denied: User is not a member of this group");
        }
        
        // Find existing active invite code for the group
        List<GroupInvite> activeInvites = groupInviteRepository.findValidByGroupId(groupId, LocalDateTime.now());
        
        if (activeInvites.isEmpty()) {
            // Generate new invite code if none exists
            return generateInvite(currentUserId, groupId, 30); // Default 30 days expiry
        }
        
        // Return the first active invite code
        GroupInvite activeInvite = activeInvites.get(0);
        String code = activeInvite.getInviteCode() != null ? activeInvite.getInviteCode() : activeInvite.getToken();
        return new InviteResponse(
            code,
            activeInvite.getExpiresAt(),
            activeInvite.getMaxUses(),
            activeInvite.getCurrentUses()
        );
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

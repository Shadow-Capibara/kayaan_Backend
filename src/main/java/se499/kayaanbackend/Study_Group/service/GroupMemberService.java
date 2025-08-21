package se499.kayaanbackend.Study_Group.service;

import java.util.List;

import se499.kayaanbackend.Study_Group.dto.MemberResponse;
import se499.kayaanbackend.Study_Group.dto.UpdateMemberRoleRequest;

public interface GroupMemberService {
    
    /**
     * Gets all members of a group
     */
    List<MemberResponse> getGroupMembers(Integer currentUserId, Integer groupId);
    
    /**
     * Updates a member's role (owner/moderator only)
     */
    MemberResponse updateMemberRole(Integer currentUserId, Integer groupId, Integer memberId, UpdateMemberRoleRequest request);
    
    /**
     * Removes a member from the group
     */
    void removeMember(Integer currentUserId, Integer groupId, Integer userId);
    
    /**
     * Invites a user by email (stub - email sending to be implemented later)
     */
    void inviteByEmail(Integer currentUserId, Integer groupId, String email);
}

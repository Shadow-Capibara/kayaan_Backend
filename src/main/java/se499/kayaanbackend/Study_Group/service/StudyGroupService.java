package se499.kayaanbackend.Study_Group.service;




import java.util.List;

import se499.kayaanbackend.Study_Group.dto.CreateGroupRequest;
import se499.kayaanbackend.Study_Group.dto.InviteResponse;
import se499.kayaanbackend.Study_Group.dto.StudyGroupResponse;

public interface StudyGroupService {
    
    /**
     * Creates a new study group
     */
    StudyGroupResponse createGroup(Integer currentUserId, CreateGroupRequest request);
    
    /**
     * Gets all groups that the current user is a member of
     */
    List<StudyGroupResponse> getMyGroups(Integer currentUserId);
    
    /**
     * Gets a specific group by ID (user must be a member)
     */
    StudyGroupResponse getGroup(Integer currentUserId, Integer groupId);
    
    /**
     * Joins a group using an invite token
     */
    StudyGroupResponse joinByToken(Integer currentUserId, String token);
    
    /**
     * Leaves a group (owner cannot leave if they are the only member)
     */
    void leaveGroup(Integer currentUserId, Integer groupId);
    
    /**
     * Deletes a group (owner only)
     */
    void deleteGroup(Integer currentUserId, Integer groupId);
    
    /**
     * Generates an invite token for a group
     */
    InviteResponse generateInvite(Integer currentUserId, Integer groupId, int expiryDays);
    
    /**
     * Validates an invite token without joining
     */
    InviteResponse validateInviteToken(String token);
}

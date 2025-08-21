package se499.kayaanbackend.Study_Group.service;

public interface GroupNotificationService {
    
    /**
     * Notifies all group members about a new message
     */
    void notifyNewMessage(Integer groupId, Integer senderId, String message);
    
    /**
     * Notifies group members about a new member joining
     */
    void notifyMemberJoined(Integer groupId, Integer newMemberId);
    
    /**
     * Notifies group members about a member leaving
     */
    void notifyMemberLeft(Integer groupId, Integer memberId);
    
    /**
     * Notifies group members about content updates
     */
    void notifyContentUpdate(Integer groupId, Integer updaterId, String contentType);
}

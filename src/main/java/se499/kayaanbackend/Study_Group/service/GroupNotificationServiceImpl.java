package se499.kayaanbackend.Study_Group.service;

import java.util.List;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.Study_Group.GroupMember;
import se499.kayaanbackend.Study_Group.repository.GroupMemberRepository;
import se499.kayaanbackend.security.user.UserRepository;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupNotificationServiceImpl implements GroupNotificationService {
    
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;
    
    @Override
    public void notifyNewMessage(Integer groupId, Integer senderId, String message) {
        try {
            List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
            
            for (GroupMember member : members) {
                if (!member.getUserId().equals(senderId)) {
                    // TODO: Implement actual notification system (WebSocket, email, push notification)
                    log.info("Notifying user {} about new message in group {}: {}", 
                            member.getUserId(), groupId, message.substring(0, Math.min(message.length(), 50)));
                }
            }
        } catch (Exception e) {
            log.error("Error notifying group members about new message", e);
        }
    }
    
    @Override
    public void notifyMemberJoined(Integer groupId, Integer newMemberId) {
        try {
            List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
            
            for (GroupMember member : members) {
                if (!member.getUserId().equals(newMemberId)) {
                    // TODO: Implement actual notification system
                    log.info("Notifying user {} about new member {} joining group {}", 
                            member.getUserId(), newMemberId, groupId);
                }
            }
        } catch (Exception e) {
            log.error("Error notifying group members about new member", e);
        }
    }
    
    @Override
    public void notifyMemberLeft(Integer groupId, Integer memberId) {
        try {
            List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
            
            for (GroupMember member : members) {
                // TODO: Implement actual notification system
                log.info("Notifying user {} about member {} leaving group {}", 
                        member.getUserId(), memberId, groupId);
            }
            
            // TODO: Send notification to the removed member
            log.info("Sending removal notification to user {} about being removed from group {}", 
                    memberId, groupId);
        } catch (Exception e) {
            log.error("Error notifying group members about member leaving", e);
        }
    }
    
    @Override
    public void notifyContentUpdate(Integer groupId, Integer updaterId, String contentType) {
        try {
            List<GroupMember> members = groupMemberRepository.findByGroupId(groupId);
            
            for (GroupMember member : members) {
                if (!member.getUserId().equals(updaterId)) {
                    // TODO: Implement actual notification system
                    log.info("Notifying user {} about {} update in group {} by user {}", 
                            member.getUserId(), contentType, groupId, updaterId);
                }
            }
        } catch (Exception e) {
            log.error("Error notifying group members about content update", e);
        }
    }
}

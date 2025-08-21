package se499.kayaanbackend.Study_Group.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import se499.kayaanbackend.Study_Group.GroupMember;
import se499.kayaanbackend.Study_Group.StudyGroup;
import se499.kayaanbackend.Study_Group.dto.MessageRequest;
import se499.kayaanbackend.Study_Group.dto.MessageResponse;
import se499.kayaanbackend.Study_Group.entity.GroupMessage;
import se499.kayaanbackend.Study_Group.repository.GroupMemberRepository;
import se499.kayaanbackend.Study_Group.repository.GroupMessageRepository;
import se499.kayaanbackend.Study_Group.repository.StudyGroupRepository;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.security.user.UserRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class GroupMessageServiceImpl implements GroupMessageService {
    
    private final GroupMessageRepository groupMessageRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final UserRepository userRepository;
    private final GroupNotificationService notificationService;
    
    @Override
    public MessageResponse sendMessage(Integer currentUserId, Integer groupId, MessageRequest request) {
        // Check if user is a member of the group
        GroupMember membership = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new RuntimeException("Access denied: User is not a member of this group"));
        
        User user = userRepository.findById(currentUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        GroupMessage message = GroupMessage.builder()
                .studyGroup(group)
                .user(user)
                .content(request.content())
                .messageType(request.messageType())
                .createdAt(LocalDateTime.now())
                .build();
        
        GroupMessage savedMessage = groupMessageRepository.save(message);
        
        // Send notification to other group members
        notificationService.notifyNewMessage(groupId, currentUserId, request.content());
        
        return mapToResponse(savedMessage);
    }
    
    @Override
    public List<MessageResponse> getMessages(Integer currentUserId, Integer groupId, int page, int size) {
        // Check if user is a member of the group
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new RuntimeException("Access denied: User is not a member of this group");
        }
        
        PageRequest pageRequest = PageRequest.of(page, size);
        List<GroupMessage> messages = groupMessageRepository.findByStudyGroupIdOrderByCreatedAtDesc(groupId, pageRequest);
        
        return messages.stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
    
    @Override
    public MessageResponse updateMessage(Integer currentUserId, Integer groupId, Long messageId, MessageRequest request) {
        GroupMessage message = groupMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        // Check if message belongs to the group
        if (!message.getStudyGroup().getId().equals(groupId)) {
            throw new RuntimeException("Message does not belong to this group");
        }
        
        // Check if user is the sender
        if (!message.getUser().getId().equals(currentUserId)) {
            throw new RuntimeException("Access denied: Only the sender can edit this message");
        }
        
        // Check if user is still a member
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, currentUserId)) {
            throw new RuntimeException("Access denied: User is not a member of this group");
        }
        
        message.setContent(request.content());
        message.setMessageType(request.messageType());
        GroupMessage updatedMessage = groupMessageRepository.save(message);
        
        // Notify about content update
        notificationService.notifyContentUpdate(groupId, currentUserId, "message update");
        
        return mapToResponse(updatedMessage);
    }
    
    @Override
    public void deleteMessage(Integer currentUserId, Integer groupId, Long messageId) {
        GroupMessage message = groupMessageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        
        // Check if message belongs to the group
        if (!message.getStudyGroup().getId().equals(groupId)) {
            throw new RuntimeException("Message does not belong to this group");
        }
        
        // Check if user is the sender or admin
        GroupMember membership = groupMemberRepository.findByGroupIdAndUserId(groupId, currentUserId)
                .orElseThrow(() -> new RuntimeException("Access denied: User is not a member of this group"));
        
        if (!message.getUser().getId().equals(currentUserId) && membership.getRole() != GroupMember.Role.admin) {
            throw new RuntimeException("Access denied: Only the sender or admin can delete this message");
        }
        
        groupMessageRepository.delete(message);
        
        // Notify about content update
        notificationService.notifyContentUpdate(groupId, currentUserId, "message deletion");
    }
    
    private MessageResponse mapToResponse(GroupMessage message) {
        return new MessageResponse(
                message.getId(),
                message.getStudyGroup().getId(),
                message.getUser().getId(),
                message.getUser().getFirstname() + " " + message.getUser().getLastname(),
                message.getContent(),
                message.getMessageType(),
                message.getCreatedAt(),
                message.getUpdatedAt()
        );
    }
}

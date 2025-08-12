package se499.kayaanbackend.Study_Group.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import se499.kayaanbackend.Study_Group.StudyGroup;
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
    private final StudyGroupRepository studyGroupRepository;
    private final GroupMemberRepository groupMemberRepository;
    private final UserRepository userRepository;

    @Override
    public GroupMessage save(Integer groupId, Integer userId, String content) {
        // Check if user is a member of the group
        if (!groupMemberRepository.existsByGroupIdAndUserId(groupId, userId)) {
            throw new RuntimeException("Access denied: User is not a member of this group");
        }
        
        StudyGroup group = studyGroupRepository.findById(groupId)
                .orElseThrow(() -> new RuntimeException("Group not found"));
        
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));
        
        GroupMessage message = GroupMessage.builder()
                .studyGroup(group)
                .user(user)
                .content(content)
                .createdAt(LocalDateTime.now())
                .build();
        
        return groupMessageRepository.save(message);
    }
    
    public MessageResponse saveAndReturnResponse(Integer groupId, Integer userId, String content) {
        GroupMessage savedMessage = save(groupId, userId, content);
        
        return new MessageResponse(
                savedMessage.getId(),
                savedMessage.getUser().getId(),
                savedMessage.getContent(),
                savedMessage.getCreatedAt()
        );
    }
}

package se499.kayaanbackend.Study_Group.service;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import se499.kayaanbackend.Study_Group.entity.GroupMessage;
import se499.kayaanbackend.Study_Group.repository.GroupMessageRepository;
import se499.kayaanbackend.Study_Group.repository.StudyGroupRepository;
import se499.kayaanbackend.security.user.UserRepository;

@Service
@RequiredArgsConstructor
public class GroupMessageServiceImpl implements GroupMessageService {
    private final GroupMessageRepository groupMessageRepository;
    private final StudyGroupRepository studyGroupRepository;
    private final UserRepository userRepository;

    @Override
    public GroupMessage save(Integer groupId, Integer userId, String content) {
        // Stub implementation - return mock GroupMessage
        GroupMessage message = new GroupMessage();
        message.setId(1L);

        
        message.setContent(content);
        message.setCreatedAt(java.time.LocalDateTime.now());
        return message;
    }
}

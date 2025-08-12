package se499.kayaanbackend.Study_Group.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import se499.kayaanbackend.Study_Group.dto.MessageResponse;
import se499.kayaanbackend.Study_Group.service.GroupMessageServiceImpl;

@Controller
@RequiredArgsConstructor
public class GroupChatController {
    private final GroupMessageServiceImpl groupMessageService;

    @MessageMapping("/groups/{groupId}/chat")
    @SendTo("/topic/groups/{groupId}")
    public MessageResponse sendMessage(@Payload MessageRequest request) {
        try {
            // Extract groupId from the path variable (you might need to adjust this based on your WebSocket setup)
            Integer groupId = extractGroupIdFromContext();
            Integer userId = extractUserIdFromContext();
            
            return groupMessageService.saveAndReturnResponse(groupId, userId, request.content());
        } catch (Exception e) {
            throw new RuntimeException("Failed to save message", e);
        }
    }

    public record MessageRequest(String content) {}
    
    // TODO: Implement these methods based on your WebSocket context setup
    private Integer extractGroupIdFromContext() {
        // This should extract groupId from WebSocket session or message context
        throw new RuntimeException("Not implemented - need WebSocket context setup");
    }
    
    private Integer extractUserIdFromContext() {
        // This should extract userId from WebSocket session or message context
        throw new RuntimeException("Not implemented - need WebSocket context setup");
    }
}

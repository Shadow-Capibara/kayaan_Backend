package se499.kayaanbackend.Study_Group.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import lombok.RequiredArgsConstructor;
import se499.kayaanbackend.Study_Group.service.GroupMessageService;

@Controller
@RequiredArgsConstructor
public class GroupChatController {
    private final GroupMessageService groupMessageService;

    @MessageMapping("/groups/{groupId}/send")
    @SendTo("/topic/groups/{groupId}")
    public GroupMessageResponse sendMessage(@Payload GroupMessageRequest request) {
        try {
            // Stub implementation - return mock response
            return GroupMessageResponse.builder()
                    .id(1L)
                    .groupId(request.groupId())
                    .userId(request.userId())
                    .userName("Test User")
                    .content(request.content())
                    .createdAt(java.time.LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            throw new RuntimeException("Failed to save message", e);
        }
    }

    public record GroupMessageRequest(Integer groupId, Integer userId, String content) {}

    public static class GroupMessageResponse {
        private Long id;
        private Integer groupId;
        private Integer userId;
        private String userName;
        private String content;
        private java.time.LocalDateTime createdAt;

        public static GroupMessageResponseBuilder builder() {
            return new GroupMessageResponseBuilder();
        }

        public static class GroupMessageResponseBuilder {
            private Long id;
            private Integer groupId;
            private Integer userId;
            private String userName;
            private String content;
            private java.time.LocalDateTime createdAt;

            public GroupMessageResponseBuilder id(Long id) {
                this.id = id;
                return this;
            }

            public GroupMessageResponseBuilder groupId(Integer groupId) {
                this.groupId = groupId;
                return this;
            }

            public GroupMessageResponseBuilder userId(Integer userId) {
                this.userId = userId;
                return this;
            }

            public GroupMessageResponseBuilder userName(String userName) {
                this.userName = userName;
                return this;
            }

            public GroupMessageResponseBuilder content(String content) {
                this.content = content;
                return this;
            }

            public GroupMessageResponseBuilder createdAt(java.time.LocalDateTime createdAt) {
                this.createdAt = createdAt;
                return this;
            }

            public GroupMessageResponse build() {
                GroupMessageResponse response = new GroupMessageResponse();
                response.id = this.id;
                response.groupId = this.groupId;
                response.userId = this.userId;
                response.userName = this.userName;
                response.content = this.content;
                response.createdAt = this.createdAt;
                return response;
            }
        }
    }
}

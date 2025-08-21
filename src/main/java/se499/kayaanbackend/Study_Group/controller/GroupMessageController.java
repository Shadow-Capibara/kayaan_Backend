package se499.kayaanbackend.Study_Group.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import se499.kayaanbackend.Study_Group.dto.MessageRequest;
import se499.kayaanbackend.Study_Group.dto.MessageResponse;
import se499.kayaanbackend.Study_Group.service.GroupMessageService;
import se499.kayaanbackend.security.user.User;

@RestController
@RequestMapping("/api/groups")
@RequiredArgsConstructor
public class GroupMessageController {
    
    private final GroupMessageService groupMessageService;
    
    @PostMapping("/{groupId}/messages")
    public ResponseEntity<MessageResponse> sendMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId,
            @RequestBody MessageRequest request) {
        MessageResponse message = groupMessageService.sendMessage(currentUser.getId(), groupId, request);
        return ResponseEntity.ok(message);
    }
    
    @GetMapping("/{groupId}/messages")
    public ResponseEntity<List<MessageResponse>> getMessages(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        List<MessageResponse> messages = groupMessageService.getMessages(currentUser.getId(), groupId, page, size);
        return ResponseEntity.ok(messages);
    }
    
    @PutMapping("/{groupId}/messages/{messageId}")
    public ResponseEntity<MessageResponse> updateMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId,
            @PathVariable Long messageId,
            @RequestBody MessageRequest request) {
        MessageResponse message = groupMessageService.updateMessage(currentUser.getId(), groupId, messageId, request);
        return ResponseEntity.ok(message);
    }
    
    @DeleteMapping("/{groupId}/messages/{messageId}")
    public ResponseEntity<Void> deleteMessage(
            @AuthenticationPrincipal User currentUser,
            @PathVariable Integer groupId,
            @PathVariable Long messageId,
            @RequestParam(defaultValue = "false") boolean confirm) {
        
        if (!confirm) {
            throw new RuntimeException("Please confirm message deletion by setting confirm=true");
        }
        
        groupMessageService.deleteMessage(currentUser.getId(), groupId, messageId);
        return ResponseEntity.ok().build();
    }
}

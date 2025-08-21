package se499.kayaanbackend.Study_Group.service;

import java.util.List;

import se499.kayaanbackend.Study_Group.dto.MessageRequest;
import se499.kayaanbackend.Study_Group.dto.MessageResponse;

public interface GroupMessageService {
    
    /**
     * Sends a message to a study group
     */
    MessageResponse sendMessage(Integer currentUserId, Integer groupId, MessageRequest request);
    
    /**
     * Gets messages from a study group with pagination
     */
    List<MessageResponse> getMessages(Integer currentUserId, Integer groupId, int page, int size);
    
    /**
     * Updates a message (only by the sender)
     */
    MessageResponse updateMessage(Integer currentUserId, Integer groupId, Long messageId, MessageRequest request);
    
    /**
     * Deletes a message (only by the sender or admin)
     */
    void deleteMessage(Integer currentUserId, Integer groupId, Long messageId);
}

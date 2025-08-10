package se499.kayaanbackend.Study_Group.service;

import se499.kayaanbackend.Study_Group.entity.GroupMessage;

public interface GroupMessageService {
    
    /**
     * Saves a group message
     * @param groupId The study group ID
     * @param userId The user ID who sent the message
     * @param content The message content
     * @return The saved GroupMessage
     */
    GroupMessage save(Integer groupId, Integer userId, String content);
}

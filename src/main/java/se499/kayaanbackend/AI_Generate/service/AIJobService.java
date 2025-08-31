package se499.kayaanbackend.AI_Generate.service;

import se499.kayaanbackend.AI_Generate.entity.AIDraft;
import se499.kayaanbackend.AI_Generate.entity.AIJob;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.shared.storage.StorageService;

public interface AIJobService {
    
    /**
     * Requests a signed upload URL for AI job output
     */
    StorageService.SignedUrl requestOutputUploadUrl(String jobId, String fileName, String contentType);
    
    /**
     * Creates a new AI job
     */
    AIJob createJob(User user, String jobType, String inputData);
    
    /**
     * Processes an AI job
     */
    AIJob processJob(Long jobId);
    
    /**
     * Creates a new AI draft
     */
    AIDraft createDraft(User user, String draftType, String title, String content);
    
    /**
     * Saves an AI draft
     */
    AIDraft saveDraft(Long draftId);
}

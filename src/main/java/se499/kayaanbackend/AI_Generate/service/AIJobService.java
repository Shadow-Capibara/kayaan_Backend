package se499.kayaanbackend.AI_Generate.service;

import se499.kayaanbackend.shared.storage.StorageService;

public interface AIJobService {
    
    /**
     * Requests a signed upload URL for AI job output
     * @param jobId The AI job ID
     * @param fileName The file name to upload
     * @param contentType The content type of the file
     * @return StorageService.SignedUrl containing the signed URL, path, and expiration
     */
    StorageService.SignedUrl requestOutputUploadUrl(String jobId, String fileName, String contentType);
}

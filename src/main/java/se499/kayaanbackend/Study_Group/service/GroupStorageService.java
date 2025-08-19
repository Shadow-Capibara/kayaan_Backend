package se499.kayaanbackend.Study_Group.service;

public interface GroupStorageService {
    
    /**
     * Creates a signed upload URL for group resources
     * @param groupId The study group ID
     * @param fileName The original file name
     * @param mimeType The MIME type of the file
     * @param contentLength The size of the file in bytes
     * @return UploadUrlResponse containing uploadUrl and fileUrl
     */
    UploadUrlResponse createSignedUploadUrl(Integer groupId, String fileName, String mimeType, Long contentLength);
    
    /**
     * Response DTO for signed upload URL
     */
    record UploadUrlResponse(String uploadUrl, String storagePath, String fileUrl) {}

    /**
     * Build public URL for an object path (when bucket is public)
     */
    String getPublicFileUrl(String objectPath);

    /**
     * Create short-lived signed GET URL for an object path (for private bucket preview)
     */
    String createSignedGetUrl(String objectPath, int expiresInSeconds);
}

package se499.kayaanbackend.shared.storage;

public interface StorageService {
    
    /**
     * Creates a signed upload URL for direct file upload to storage
     * @param bucket The bucket name
     * @param path The file path within the bucket
     * @param expiresInSeconds URL expiration time in seconds
     * @param contentType The content type of the file
     * @return SignedUrl containing the signed URL, path, and expiration
     */
    SignedUrl createSignedUploadUrl(String bucket, String path, int expiresInSeconds, String contentType);
    
    /**
     * Gets the public URL for a file in storage
     * @param bucket The bucket name
     * @param path The file path within the bucket
     * @return The public URL for the file
     */
    String getPublicUrl(String bucket, String path);
    
    /**
     * Deletes a file from storage
     * @param bucket The bucket name
     * @param path The file path within the bucket
     * @return true if deletion was successful
     */
    boolean delete(String bucket, String path);
    
    /**
     * Record representing a signed URL response
     * @param url The signed URL for upload
     * @param path The file path
     * @param expiresInSeconds Expiration time in seconds
     */
    record SignedUrl(String url, String path, int expiresInSeconds) {}
}

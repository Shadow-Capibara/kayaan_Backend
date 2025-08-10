package se499.kayaanbackend.shared.storage;

public class MockStorageService implements StorageService {
    
    @Override
    public SignedUrl createSignedUploadUrl(String bucket, String path, int expiresInSeconds, String contentType) {
        // Return a mock signed URL for development
        String mockUrl = "https://mock-storage.example.com/upload/" + bucket + "/" + path;
        return new SignedUrl(mockUrl, path, expiresInSeconds);
    }
    
    @Override
    public String getPublicUrl(String bucket, String path) {
        // Return a mock public URL for development
        return "https://mock-storage.example.com/public/" + bucket + "/" + path;
    }
    
    @Override
    public boolean delete(String bucket, String path) {
        // Mock delete operation - always return true
        return true;
    }
}

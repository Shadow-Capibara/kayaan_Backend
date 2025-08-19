package se499.kayaanbackend.shared.storage;

import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Component
@Profile("dev")
@Primary
public class MockStorageService implements StorageService {
    
    @Override
    public SignedUrl createSignedUploadUrl(String bucket, String path, int expiresInSeconds, String contentType) {
        // Return a mock signed URL for development
        String mockUrl = "https://mock-storage.example.com/upload/" + bucket + "/" + path;
        return new SignedUrl(mockUrl, path, expiresInSeconds);
    }
    
    @Override
    public SignedUrl createSignedGetUrl(String bucket, String path, int expiresInSeconds) {
        String mockUrl = "https://mock-storage.example.com/get/" + bucket + "/" + path + "?sig=mock";
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
    
    @Override
    public String getServiceKey() {
        // Return mock service key for development
        return "mock-service-key";
    }
}

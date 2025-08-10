package se499.kayaanbackend.AI_Generate.service;

import java.time.Instant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import se499.kayaanbackend.shared.storage.StorageService;

@Service
public class AIJobServiceImpl implements AIJobService {
    
    @Autowired
    private StorageService storageService;
    
    @Value("${kayaan.supabase.buckets.ai}")
    private String aiBucket;
    
    @Override
    public StorageService.SignedUrl requestOutputUploadUrl(String jobId, String fileName, String contentType) {
        String timestamp = String.valueOf(Instant.now().toEpochMilli());
        String path = String.format("jobs/%s/%s_%s", jobId, timestamp, fileName);
        
        return storageService.createSignedUploadUrl(
            aiBucket,
            path,
            1800, // 30 minutes for AI jobs
            contentType
        );
    }
}

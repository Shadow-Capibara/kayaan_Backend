package se499.kayaanbackend.Study_Group.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.shared.storage.StorageService;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupStorageServiceImpl implements GroupStorageService {
    
    @Value("${kayaan.supabase.buckets.library:study-content}")
    private String libraryBucket;
    
    @Value("${kayaan.supabase.signed-get-ttl:300}")
    private int signedGetTtl;
    
    @Value("${kayaan.supabase.signed-get-skew-sec:10}")
    private int signedGetSkewSec;
    
    private final StorageService storageService;
    
    @Override
    public UploadUrlResponse createSignedUploadUrl(Integer groupId, String fileName, String mimeType, Long contentLength) {
        try {
            // Generate object path: groups/{groupId}/{yyyy}/{MM}/{uuid}-{originalName}
            String objectPath = generateObjectPath(groupId, fileName);
            
            // Create signed URL via StorageService (mock or supabase adapter)
            StorageService.SignedUrl signed = storageService.createSignedUploadUrl(
                    libraryBucket,
                    objectPath,
                    600,
                    ResourceValidationUtil.normalizeContentType(mimeType)
            );
            
            // Generate file URL for later access
            String fileUrl = storageService.getPublicUrl(libraryBucket, objectPath);
            
            return new UploadUrlResponse(signed.url(), objectPath, fileUrl);
            
        } catch (Exception e) {
            log.error("Failed to create signed upload URL for group {} and file {}", groupId, fileName, e);
            throw new RuntimeException("Failed to create upload URL", e);
        }
    }
    
    private String generateObjectPath(Integer groupId, String fileName) {
        LocalDateTime now = LocalDateTime.now();
        String year = String.valueOf(now.getYear());
        String month = String.format("%02d", now.getMonthValue());
        String uuid = UUID.randomUUID().toString();
        
        // Sanitize fileName to prevent path traversal
        String sanitizedFileName = fileName.replaceAll("[^a-zA-Z0-9.-]", "_");
        
        return String.format("groups/%d/%s/%s/%s-%s", groupId, year, month, uuid, sanitizedFileName);
    }
    
    // Removed direct HTTP call/generation; delegated to StorageService

    @Override
    public String getPublicFileUrl(String objectPath) {
        return storageService.getPublicUrl(libraryBucket, objectPath);
    }

    @Override
    public String createSignedGetUrl(String objectPath, int expiresInSeconds) {
        int ttl = expiresInSeconds > 0 ? expiresInSeconds : signedGetTtl;
        // เผื่อเวลา clock skew เล็กน้อย แต่ไม่ให้เกิน TTL
        int safeTtl = Math.max(60, Math.min(ttl, ttl + Math.max(0, signedGetSkewSec)));
        return storageService.createSignedGetUrl(libraryBucket, objectPath, safeTtl).url();
    }
}

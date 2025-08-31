package se499.kayaanbackend.Study_Group.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class GroupStorageServiceImpl implements GroupStorageService {
    
    @Value("${supabase.url}")
    private String supabaseUrl;
    
    @Value("${supabase.serviceKey}")
    private String supabaseServiceKey;
    
    @Value("${supabase.buckets.library:library}")
    private String libraryBucket;
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    @Override
    public UploadUrlResponse createSignedUploadUrl(Integer groupId, String fileName, String mimeType, Long contentLength) {
        try {
            // Generate object path: groups/{groupId}/{yyyy}/{MM}/{uuid}-{originalName}
            String objectPath = generateObjectPath(groupId, fileName);
            
            // Create signed URL using Supabase Storage API
            String signedUrl = createSupabaseSignedUrl(objectPath, mimeType, contentLength);
            
            // Generate file URL for later access
            String fileUrl = generateFileUrl(objectPath);
            
            return new UploadUrlResponse(signedUrl, fileUrl);
            
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
    
    private String createSupabaseSignedUrl(String objectPath, String mimeType, Long contentLength) {
        String url = supabaseUrl + "/storage/v1/object/sign/" + libraryBucket + "/" + objectPath;
        
        HttpHeaders headers = new HttpHeaders();
        headers.set("apikey", supabaseServiceKey);
        headers.set("Authorization", "Bearer " + supabaseServiceKey);
        headers.set("Content-Type", "application/json");
        
        // Request body for signed URL
        String requestBody = String.format(
            "{\"expiresIn\": 3600, \"transform\": {\"width\": null, \"height\": null}}"
        );
        
        HttpEntity<String> entity = new HttpEntity<>(requestBody, headers);
        
        ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
        
        if (response.getStatusCode().is2xxSuccessful()) {
            try {
                JsonNode jsonNode = objectMapper.readTree(response.getBody());
                return jsonNode.get("signedURL").asText();
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse signed URL response", e);
            }
        } else {
            throw new RuntimeException("Failed to create signed URL: " + response.getStatusCode());
        }
    }
    
    private String generateFileUrl(String objectPath) {
        return supabaseUrl + "/storage/v1/object/public/" + libraryBucket + "/" + objectPath;
    }
}

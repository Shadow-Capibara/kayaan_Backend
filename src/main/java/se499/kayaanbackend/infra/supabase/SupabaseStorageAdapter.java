package se499.kayaanbackend.infra.supabase;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import se499.kayaanbackend.shared.storage.StorageService;

@Component
@Primary
public class SupabaseStorageAdapter implements StorageService {
    
    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String supabaseUrl;
    private final String serviceKey;
    
    public SupabaseStorageAdapter(
            @Value("${kayaan.supabase.url}") String supabaseUrl,
            @Value("${kayaan.supabase.serviceKey}") String serviceKey) {
        this.supabaseUrl = supabaseUrl;
        this.serviceKey = serviceKey;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    @Override
    public SignedUrl createSignedUploadUrl(String bucket, String path, int expiresInSeconds, String contentType) {
        try {
            String url = supabaseUrl + "/storage/v1/object/sign/" + bucket + "/" + path;
            
            System.out.println("Supabase URL: " + url);
            System.out.println("Bucket: " + bucket);
            System.out.println("Path: " + path);
            System.out.println("ContentType: " + contentType);
            
            Map<String, Object> requestBody = Map.of(
                "expiresIn", expiresInSeconds,
                "contentType", contentType
            );
            
            String jsonBody = objectMapper.writeValueAsString(requestBody);
            System.out.println("Request body: " + jsonBody);
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + serviceKey)
                    .header("apikey", serviceKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            System.out.println("Response status: " + response.statusCode());
            System.out.println("Response body: " + response.body());
            
            if (response.statusCode() == 200) {
                SignedUrlResponse signedUrlResponse = objectMapper.readValue(response.body(), SignedUrlResponse.class);
                return new SignedUrl(signedUrlResponse.signedURL, path, expiresInSeconds);
            } else {
                throw new RuntimeException("Failed to create signed URL. Status: " + response.statusCode() + ", Body: " + response.body());
            }
            
        } catch (Exception e) {
            System.err.println("Exception in createSignedUploadUrl: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Error creating signed upload URL", e);
        }
    }
    
    @Override
    public String getPublicUrl(String bucket, String path) {
        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + path;
    }
    
    @Override
    public boolean delete(String bucket, String path) {
        try {
            String url = supabaseUrl + "/storage/v1/object/" + bucket + "/" + path;
            
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .header("Authorization", "Bearer " + serviceKey)
                    .header("apikey", serviceKey)
                    .DELETE()
                    .build();
            
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            
            return response.statusCode() == 200;
            
        } catch (Exception e) {
            throw new RuntimeException("Error deleting file", e);
        }
    }
    
    /**
     * DTO for Supabase signed URL response
     */
    public static class SignedUrlResponse {
        public String signedURL;
        public String path;
    }
}

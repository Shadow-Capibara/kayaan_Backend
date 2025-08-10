package se499.kayaanbackend.infra.supabase;

import java.net.ProxySelector;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import se499.kayaanbackend.shared.storage.StorageService;

@Component
@Primary
public class SupabaseStorageAdapter implements StorageService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String supabaseUrl;
    private final String serviceKey;

    public SupabaseStorageAdapter(
            @Value("${supabase.url}") String supabaseUrl,
            @Value("${supabase.serviceKey}") String serviceKey) {
        this.supabaseUrl = supabaseUrl;
        this.serviceKey = serviceKey;
        this.httpClient = HttpClient.newBuilder()
                .proxy(ProxySelector.getDefault())
                .connectTimeout(Duration.ofSeconds(30))
                .version(HttpClient.Version.HTTP_1_1)
                .build();
        this.objectMapper = new ObjectMapper();
    }
    
    @PostConstruct
    void validateSupabaseConfig() {
        System.out.println("=== Supabase Configuration Validation ===");
        System.out.println("Supabase URL runtime: [" + supabaseUrl + "]");
        System.out.println("Service key length: " + (serviceKey == null ? "null" : serviceKey.length()));
        System.out.println("Service key dotCount: " + (serviceKey == null ? "null" : serviceKey.chars().filter(ch -> ch=='.').count()));
        
        if (supabaseUrl == null || !supabaseUrl.startsWith("https://") || supabaseUrl.contains(" ")) {
            throw new IllegalStateException("supabase.url is invalid: " + supabaseUrl);
        }
        if (serviceKey == null || serviceKey.chars().filter(ch -> ch=='.').count() != 2) {
            throw new IllegalStateException("supabase.serviceKey is not a valid JWT (missing 2 dots). Length: " + 
                (serviceKey == null ? "null" : serviceKey.length()) + 
                ", Dot count: " + (serviceKey == null ? "null" : serviceKey.chars().filter(ch -> ch=='.').count()));
        }
        System.out.println("=== Supabase Configuration Validation PASSED ===");
    }
    
    @Override
    public StorageService.SignedUrl createSignedUploadUrl(String bucket, String path, int expiresInSeconds, String contentType) {
        try {
            // encode ทีละ segment
            String[] segments = path.split("/");
            String encodedPath = Arrays.stream(segments)
                .map(s -> URLEncoder.encode(s, StandardCharsets.UTF_8))
                .collect(Collectors.joining("/"));

            String url = supabaseUrl + "/storage/v1/object/upload/sign/" + 
                         URLEncoder.encode(bucket, StandardCharsets.UTF_8) + "/" + encodedPath;

            String key = serviceKey.trim();

            // quick sanity logs
            long dotCount = key.chars().filter(ch -> ch == '.').count();
            System.out.println("Supabase URL: " + supabaseUrl);
            System.out.println("Service key dotCount==2? " + (dotCount == 2));
            System.out.println("Key prefix: " + (key.length() > 8 ? key.substring(0,8) : "***"));

            Map<String, Object> requestBody = Map.of(
                "expiresIn", expiresInSeconds,
                "contentType", contentType,
                "upsert", true
            );

            String jsonBody = objectMapper.writeValueAsString(requestBody);

            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("Authorization", "Bearer " + key) // ต้องเป็น Bearer
                .header("apikey", key)                     // และ apikey
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Response status: " + response.statusCode());
            System.out.println("Response body: " + response.body());

            if (response.statusCode() / 100 == 2) {
                // ชื่อฟิลด์ของ Supabase เป็น camelCase: signedUrl
                SignedUrlResponse body = objectMapper.readValue(response.body(), SignedUrlResponse.class);
                System.out.println("Parsed response - url: " + body.url);
                System.out.println("Parsed response - signedUrl: " + body.signedUrl);
                System.out.println("Parsed response - signedURL: " + body.signedURL);
                System.out.println("Parsed response - token: " + body.token);
                // รองรับทั้ง signedUrl, signedURL, url และ token เผื่อเวอร์ชันต่างกัน
                String signed = body.url != null ? body.url : 
                               body.signedUrl != null ? body.signedUrl : 
                               body.signedURL != null ? body.signedURL : null;
                System.out.println("Selected signed URL: " + signed);
                // ถ้า signed URL เป็น relative path ให้เพิ่ม base URL
                if (signed != null && signed.startsWith("/")) {
                    signed = supabaseUrl + signed;
                    System.out.println("Full signed URL: " + signed);
                }
                if (signed == null) {
                    System.err.println("No signed URL found in response. Response body: " + response.body());
                    throw new RuntimeException("No signed URL found in response");
                }
                return new StorageService.SignedUrl(signed, path, expiresInSeconds);
            }
            throw new RuntimeException("Failed to create signed URL. Status: " + response.statusCode() + ", Body: " + response.body());

        } catch (Exception e) {
            System.err.println("Exception in createSignedUploadUrl: " + e.getMessage());
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
        public String signedUrl;  // ← ตามเอกสารปัจจุบัน
        public String signedURL;  // ← กันเหนียว
        public String url;        // ← response format ปัจจุบัน
        public String token;      // ← response format ปัจจุบัน
        public String path;
    }
}

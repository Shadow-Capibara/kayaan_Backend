package se499.kayaanbackend.infra.supabase;

import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.annotation.PostConstruct;
import se499.kayaanbackend.shared.storage.StorageService;

@Component
@Profile("!dev")
@Primary
public class SupabaseStorageAdapter implements StorageService {

    private final HttpClient httpClient;
    private final ObjectMapper objectMapper;
    private final String supabaseUrl;
    private final String serviceKey;

    public SupabaseStorageAdapter(
            @Value("${kayaan.supabase.url}") String supabaseUrl,
            @Value("${kayaan.supabase.service-key}") String serviceKey) {
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
            throw new IllegalStateException("kayaan.supabase.url is invalid: " + supabaseUrl);
        }
        if (serviceKey == null || serviceKey.chars().filter(ch -> ch=='.').count() != 2) {
            throw new IllegalStateException("kayaan.supabase.service-key is not a valid JWT (missing 2 dots). Length: " + 
                (serviceKey == null ? "null" : serviceKey.length()) + 
                ", Dot count: " + (serviceKey == null ? "null" : serviceKey.chars().filter(ch -> ch=='.').count()));
        }
        System.out.println("=== Supabase Configuration Validation PASSED ===");
    }
    
    @Override
public StorageService.SignedUrl createSignedUploadUrl(String bucket, String path, int expiresInSeconds, String contentType) {
    try {
        // 1) สร้าง object (raw) — ไม่มี bucket นำหน้า
        String object = path;
        if (object.startsWith("/")) object = object.substring(1);
        if (object.startsWith(bucket + "/")) object = object.substring((bucket + "/").length());
        object = object.replaceAll("/{2,}", "/");

        // 2) encode เป็น URL path segment ทีละส่วน เพื่อเรียก endpoint sign
        String encodedObject = java.util.Arrays.stream(object.split("/"))
            .map(s -> java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8))
            .collect(java.util.stream.Collectors.joining("/"));
        String encodedBucket = java.net.URLEncoder.encode(bucket, java.nio.charset.StandardCharsets.UTF_8);

        String url = supabaseUrl + "/storage/v1/object/upload/sign/" + encodedBucket + "/" + encodedObject;

        String normalizedContentType = (contentType == null || contentType.isBlank()) ? "application/octet-stream" : contentType;
        Map<String, Object> body = Map.of(
            "contentType", normalizedContentType,
            "upsert", true
        );

        HttpRequest request = HttpRequest.newBuilder(URI.create(url))
            .header("Authorization", "Bearer " + serviceKey.trim())
            .header("apikey", serviceKey.trim())
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
            .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() / 100 != 2) {
            throw new RuntimeException("Sign failed: " + response.statusCode() + " " + response.body());
        }

        SignedUrlResponse r = objectMapper.readValue(response.body(), SignedUrlResponse.class);
        String signed = r.url != null ? r.url : (r.signedUrl != null ? r.signedUrl : r.signedURL);
        if (signed == null) throw new RuntimeException("No signed URL in response");
        if (signed.startsWith("/")) signed = supabaseUrl + "/storage/v1" + signed;

        // (debug) แตก token ดูให้มั่นใจว่า "url" = bucket + "/" + object (raw)
        try {
            String token = signed.substring(signed.indexOf("token=") + 6);
            String payload = new String(java.util.Base64.getUrlDecoder().decode(token.split("\\.")[1]));
            String urlInToken = objectMapper.readTree(payload).get("url").asText(); // ควรได้ "avatars/users/.../xxx 1.jpg"
            System.out.println("DBG token.url=" + urlInToken + " | expected=" + bucket + "/" + object);
        } catch (Exception ignore) {}

        return new SignedUrl(signed, object, expiresInSeconds); // เก็บ object (raw) ไว้ใช้ตอนประกาศ public URL
    } catch (Exception e) {
        throw new RuntimeException("Error creating signed upload URL", e);
    }
}
    
    @Override
    public StorageService.SignedUrl createSignedGetUrl(String bucket, String path, int expiresInSeconds) {
        try {
            String object = path;
            if (object.startsWith("/")) object = object.substring(1);
            if (object.startsWith(bucket + "/")) object = object.substring((bucket + "/").length());
            object = object.replaceAll("/{2,}", "/");

            String encodedObject = java.util.Arrays.stream(object.split("/"))
                .map(s -> java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8))
                .collect(java.util.stream.Collectors.joining("/"));
            String encodedBucket = java.net.URLEncoder.encode(bucket, java.nio.charset.StandardCharsets.UTF_8);

            String url = supabaseUrl + "/storage/v1/object/sign/" + encodedBucket + "/" + encodedObject;

            Map<String, Object> body = Map.of(
                "expiresIn", Math.max(60, expiresInSeconds)
            );

            HttpRequest request = HttpRequest.newBuilder(URI.create(url))
                .header("Authorization", "Bearer " + serviceKey.trim())
                .header("apikey", serviceKey.trim())
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(objectMapper.writeValueAsString(body)))
                .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() / 100 != 2) {
                throw new RuntimeException("Sign GET failed: " + response.statusCode() + " " + response.body());
            }

            SignedUrlResponse r = objectMapper.readValue(response.body(), SignedUrlResponse.class);
            String signed = r.signedUrl != null ? r.signedUrl : (r.signedURL != null ? r.signedURL : r.url);
            if (signed == null) throw new RuntimeException("No signed GET URL in response");
            if (signed.startsWith("/")) signed = supabaseUrl + "/storage/v1" + signed;

            return new StorageService.SignedUrl(signed, object, expiresInSeconds);
        } catch (Exception e) {
            throw new RuntimeException("Error creating signed GET URL", e);
        }
    }
    
    @Override
    public String getPublicUrl(String bucket, String path) {
        return supabaseUrl + "/storage/v1/object/public/" + bucket + "/" + path;
    }
    
    @Override
    public String getServiceKey() {
        return serviceKey;
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

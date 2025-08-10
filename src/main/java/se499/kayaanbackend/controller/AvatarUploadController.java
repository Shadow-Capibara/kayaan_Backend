package se499.kayaanbackend.controller;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api")
@Slf4j
public class AvatarUploadController {

    private final HttpClient http = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(20))
            .build();

    @PostMapping(value = "/avatar/upload-proxy", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> proxyUpload(
            @RequestPart("file") MultipartFile file,
            @RequestParam("signedUrl") String signedUrl
    ) throws Exception {

        log.info("Proxy upload request - File: {}, Size: {} bytes", 
                file.getOriginalFilename(), file.getSize());

        if (file.isEmpty()) {
            log.warn("Empty file received");
            return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
        }
        
        if (file.getSize() > 5 * 1024 * 1024) { // 5MB guard
            log.warn("File too large: {} bytes", file.getSize());
            return ResponseEntity.badRequest().body(Map.of("error", "File too large (>5MB)"));
        }
        
        final String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

        log.info("Uploading to Supabase - Content-Type: {}, SignedUrl: {}", contentType, signedUrl);

        HttpRequest req = HttpRequest.newBuilder(URI.create(signedUrl))
                .header("Content-Type", contentType)   // สำคัญ! ห้ามส่ง header อื่น
                .PUT(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
                .build();

        HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());

        // Supabase มักตอบ 200/201/204 เมื่ออัปโหลดสำเร็จ
        int sc = resp.statusCode();
        log.info("Supabase response - Status: {}, Body: {}", sc, resp.body());
        
        if (sc >= 200 && sc < 300) {
            return ResponseEntity.ok().body(Map.of("message", "Upload successful"));
        }
        
        return ResponseEntity.status(sc).body(Map.of("error", "Upload failed", "details", resp.body()));
    }
}

package se499.kayaanbackend.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@RestController
@RequestMapping("/api")
@Slf4j
public class AvatarUploadController {

  private final HttpClient http = HttpClient.newBuilder()
      .connectTimeout(Duration.ofSeconds(20))
      .build();

  @PostMapping(
      path = {"/avatar/upload-proxy", "/users/{id}/avatar-upload-proxy"},
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE
  )
  public ResponseEntity<?> proxyUpload(
      @RequestPart("file") MultipartFile file,
      @RequestParam("signedUrl") String signedUrl
  ) throws Exception {

    if (file.isEmpty()) return ResponseEntity.badRequest().body(Map.of("error", "File is empty"));
    if (file.getSize() > 5 * 1024 * 1024) return ResponseEntity.badRequest().body(Map.of("error", "File too large (>5MB)"));

    String contentType = file.getContentType() != null ? file.getContentType() : "application/octet-stream";

    log.info("Proxy uploading to Supabase: ct={}, size={} bytes", contentType, file.getSize());

    HttpRequest req = HttpRequest.newBuilder(URI.create(signedUrl))
        .header("Content-Type", contentType)
        .PUT(HttpRequest.BodyPublishers.ofByteArray(file.getBytes()))
        .build();

    HttpResponse<String> resp = http.send(req, HttpResponse.BodyHandlers.ofString());
    log.info("Supabase resp: {} {}", resp.statusCode(), resp.body());

    if (resp.statusCode() >= 200 && resp.statusCode() < 300) {
      return ResponseEntity.ok(Map.of("message", "Upload successful"));
    }
    return ResponseEntity.status(resp.statusCode()).body(Map.of("error", "Upload failed", "details", resp.body()));
  }
}

package com.kayaan.storage;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/files")
@RequiredArgsConstructor
@Validated
public class FileController {
    private final StorageService storageService;

    @PostMapping("/presign")
    public ResponseEntity<PresignResponse> presign(@RequestBody PresignRequest req) {
        var key = req.getFilename();
        var result = storageService.presignPut(key, req.getMime());
        return ResponseEntity.ok(new PresignResponse(result.key(), result.uploadUrl()));
    }

    @Data
    public static class PresignRequest {
        @NotBlank
        private String filename;
        @NotBlank
        private String mime;
    }

    @Data
    public static class PresignResponse {
        private final String key;
        private final String uploadUrl;
    }
}



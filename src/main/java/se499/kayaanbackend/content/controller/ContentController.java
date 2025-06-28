package se499.kayaanbackend.content.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.content.dto.ContentDto;
import se499.kayaanbackend.content.service.ContentInformationService;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ContentController {

    private final ContentInformationService contentService;

    @GetMapping("/content/{id}")
    public ResponseEntity<ContentDto> getContent(@PathVariable Long id) {
        return ResponseEntity.ok(contentService.getContent(id));
    }

    @GetMapping("/users/{userId}/contents")
    public ResponseEntity<List<ContentDto>> getUserContents(@PathVariable Long userId) {
        return ResponseEntity.ok(contentService.getUserContents(userId));
    }

    @DeleteMapping("/content/{id}")
    public ResponseEntity<Void> deleteContent(@PathVariable Long id) {
        contentService.softDelete(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/content/{id}/restore")
    public ResponseEntity<Void> restoreContent(@PathVariable Long id) {
        contentService.restore(id);
        return ResponseEntity.ok().build();
    }
}

package se499.kayaanbackend.content.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.content.dto.FlashcardDto;
import se499.kayaanbackend.content.service.FlashcardService;

@RestController
@RequestMapping("/api/flashcard")
@RequiredArgsConstructor
public class FlashcardController {

    private final FlashcardService flashcardService;

    @PostMapping
    public ResponseEntity<FlashcardDto> createFlashcard(@RequestParam Long userId, @RequestBody FlashcardDto dto) {
        return ResponseEntity.ok(flashcardService.createFlashcard(userId, dto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlashcardDto> getFlashcard(@PathVariable Long id) {
        return ResponseEntity.ok(flashcardService.getFlashcard(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlashcardDto> updateFlashcard(@PathVariable Long id, @RequestBody FlashcardDto dto) {
        return ResponseEntity.ok(flashcardService.updateFlashcard(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlashcard(@PathVariable Long id) {
        flashcardService.deleteFlashcard(id);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{id}/restore")
    public ResponseEntity<Void> restoreFlashcard(@PathVariable Long id) {
        flashcardService.restoreFlashcard(id);
        return ResponseEntity.ok().build();
    }
}

package se499.kayaanbackend.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.DTO.FlashcardRequestDTO;
import se499.kayaanbackend.DTO.FlashcardResponseDTO;
import se499.kayaanbackend.service.FlashcardService;

import java.util.List;

@RestController
@RequestMapping("/api/manual/flashcard")
@RequiredArgsConstructor
public class FlashcardController {
    private final FlashcardService flashcardService;

    @PostMapping
    public ResponseEntity<FlashcardResponseDTO> createFlashcard(
           @RequestBody FlashcardRequestDTO dto
    ) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        FlashcardResponseDTO result = flashcardService.createFlashcard(dto, username);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<FlashcardResponseDTO>> getAllFlashcards() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(flashcardService.getAllFlashcardsForUser(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlashcardResponseDTO> getFlashcardById(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        FlashcardResponseDTO dto = flashcardService.getFlashcardById(id, username);
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlashcard(@PathVariable Long id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        flashcardService.deleteFlashcard(id, username);
        return ResponseEntity.noContent().build();
    }

}

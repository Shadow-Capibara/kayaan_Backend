package se499.kayaanbackend.Manual_Generate.Flashcard.controller;

import java.util.List;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardRequestDTO;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardResponseDTO;
import se499.kayaanbackend.Manual_Generate.Flashcard.service.FlashcardService;
import se499.kayaanbackend.security.user.User;


@RestController
@RequestMapping("/api/flashcards")
public class FlashcardController {
    
    private final FlashcardService flashcardService;
    
    public FlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }
    
    @PostMapping
    public ResponseEntity<FlashcardResponseDTO> createFlashcard(
            @RequestBody FlashcardRequestDTO request,
            @AuthenticationPrincipal User currentUser) {
        // Stub implementation - return null for now
        return ResponseEntity.ok(null);

    }
    
    @GetMapping
    public ResponseEntity<List<FlashcardResponseDTO>> getAllFlashcards(
            @AuthenticationPrincipal User currentUser) {
        // Stub implementation - return empty list
        return ResponseEntity.ok(List.of());

    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FlashcardResponseDTO> getFlashcardById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        // Stub implementation - return null for now
        return ResponseEntity.ok(null);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlashcard(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        // Stub implementation - do nothing
        return ResponseEntity.ok().build();
    }

}

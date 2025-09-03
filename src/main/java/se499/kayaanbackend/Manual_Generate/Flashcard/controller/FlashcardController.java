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
@RequestMapping("/api/flashcard")
public class FlashcardController {
    
    private final FlashcardService flashcardService;
    
    public FlashcardController(FlashcardService flashcardService) {
        this.flashcardService = flashcardService;
    }
    
    @PostMapping
    public ResponseEntity<FlashcardResponseDTO> createFlashcard(
            @RequestBody FlashcardRequestDTO request,
            @AuthenticationPrincipal User currentUser) {
        FlashcardResponseDTO savedFlashcard = flashcardService.createFlashcard(request, currentUser.getUsername());
        return ResponseEntity.ok(savedFlashcard);
    }
    
    @GetMapping
    public ResponseEntity<List<FlashcardResponseDTO>> getAllFlashcards(
            @AuthenticationPrincipal User currentUser) {
        List<FlashcardResponseDTO> flashcards = flashcardService.getAllFlashcardsForUser(currentUser.getUsername());
        return ResponseEntity.ok(flashcards);
    }

    // Frontend compatible endpoint
    @GetMapping("/user/{username}")
    public ResponseEntity<List<FlashcardResponseDTO>> getAllFlashcardsForUserByUsername(
            @PathVariable String username,
            @AuthenticationPrincipal User currentUser) {
        
        // Security check - only allow users to access their own flashcards
        if (!currentUser.getUsername().equals(username)) {
            return ResponseEntity.status(403).build();
        }

        List<FlashcardResponseDTO> flashcards = flashcardService.getAllFlashcardsForUser(username);
        return ResponseEntity.ok(flashcards);
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<FlashcardResponseDTO> getFlashcardById(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        FlashcardResponseDTO flashcard = flashcardService.getFlashcardById(id, currentUser.getUsername());
        return ResponseEntity.ok(flashcard);
    }
    
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlashcard(
            @PathVariable Long id,
            @AuthenticationPrincipal User currentUser) {
        flashcardService.deleteFlashcard(id, currentUser.getUsername());
        return ResponseEntity.noContent().build();
    }
}

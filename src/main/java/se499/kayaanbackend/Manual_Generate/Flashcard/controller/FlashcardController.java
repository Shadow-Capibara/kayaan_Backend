package se499.kayaanbackend.Manual_Generate.Flashcard.controller;



import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardRequestDTO;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardResponseDTO;
import se499.kayaanbackend.Manual_Generate.contentInfo.service.ContentServiceImpl;

import java.util.List;

@RestController
@RequestMapping("/api/manual/flashcard")
@RequiredArgsConstructor
public class FlashcardController {

    private final ContentServiceImpl contentServiceImpl;

    @PostMapping
    public ResponseEntity<FlashcardResponseDTO> createFlashcard(@RequestBody FlashcardRequestDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        FlashcardResponseDTO result = contentServiceImpl.createFlashcard(dto, username);
        return ResponseEntity.ok(result);
    }

    @GetMapping
    public ResponseEntity<List<FlashcardResponseDTO>> getAllFlashcards() {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        return ResponseEntity.ok(contentServiceImpl.getAllFlashcards(username));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<FlashcardResponseDTO>> filterFlashcards(
            @RequestParam(required = false) String subject) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        if (subject != null) {
            return ResponseEntity.ok(contentServiceImpl.getFlashcardsBySubject(username, subject));
        }
        return ResponseEntity.ok(contentServiceImpl.getAllFlashcards(username));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FlashcardResponseDTO> getFlashcardById(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        FlashcardResponseDTO dto = contentServiceImpl.getFlashcardById(id, username);
        return ResponseEntity.ok(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<FlashcardResponseDTO> updateFlashcard(
            @PathVariable Integer id,
            @RequestBody FlashcardRequestDTO dto) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        FlashcardResponseDTO updated = contentServiceImpl.updateFlashcard(id, dto, username);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFlashcard(@PathVariable Integer id) {
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        contentServiceImpl.deleteFlashcard(id, username);
        return ResponseEntity.noContent().build();
    }

//    private final FlashcardService flashcardService;
//
//    @PostMapping
//    public ResponseEntity<FlashcardResponseDTO> createFlashcard(
//           @RequestBody FlashcardRequestDTO dto
//    ) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        FlashcardResponseDTO result = flashcardService.createFlashcard(dto, username);
//        return ResponseEntity.ok(result);
//    }
//
//    @GetMapping
//    public ResponseEntity<List<FlashcardResponseDTO>> getAllFlashcards() {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        return ResponseEntity.ok(flashcardService.getAllFlashcardsForUser(username));
//    }
//
//    @GetMapping("/filter")
//    public ResponseEntity<List<FlashcardResponseDTO>> filterFlashcards(
//            @RequestParam(required = false) String category,
//            @RequestParam(required = false) String subject
//    ) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        if (category != null) {
//            return ResponseEntity.ok(flashcardService.getFlashcardsByCategory(username, category));
//        }
//        if (subject != null) {
//            return ResponseEntity.ok(flashcardService.getFlashcardsBySubject(username, subject));
//        }
//        return ResponseEntity.ok(flashcardService.getAllFlashcardsForUser(username));
//    }
//
//    @GetMapping("/{id}")
//    public ResponseEntity<FlashcardResponseDTO> getFlashcardById(@PathVariable Long id) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        FlashcardResponseDTO dto = flashcardService.getFlashcardById(id, username);
//        return ResponseEntity.ok(dto);
//    }
//
//    @DeleteMapping("/{id}")
//    public ResponseEntity<Void> deleteFlashcard(@PathVariable Long id) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        flashcardService.deleteFlashcard(id, username);
//        return ResponseEntity.noContent().build();
//    }
//
//    @PutMapping("/{id}")
//    public ResponseEntity<FlashcardResponseDTO> updateFlashcard(@PathVariable Long id,
//                                                                @RequestBody FlashcardRequestDTO dto) {
//        String username = SecurityContextHolder.getContext().getAuthentication().getName();
//        FlashcardResponseDTO updated = flashcardService.updateFlashcard(id, dto, username);
//        return ResponseEntity.ok(updated);
//    }

}

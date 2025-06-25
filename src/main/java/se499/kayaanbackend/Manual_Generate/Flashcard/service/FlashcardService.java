package se499.kayaanbackend.Manual_Generate.Flashcard.service;

import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardRequestDTO;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardResponseDTO;

import java.util.List;

public interface FlashcardService {
    FlashcardResponseDTO createFlashcard(FlashcardRequestDTO dto, String username);
    List<FlashcardResponseDTO> getAllFlashcardsForUser(String username);
    List<FlashcardResponseDTO> getFlashcardsByCategory(String username, String category);
    List<FlashcardResponseDTO> getFlashcardsBySubject(String username, String subject);
    FlashcardResponseDTO getFlashcardById(Long id, String username);
    FlashcardResponseDTO updateFlashcard(Long id, FlashcardRequestDTO dto, String username);
    void deleteFlashcard(Long id, String username);
}

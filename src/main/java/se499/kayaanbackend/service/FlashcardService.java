package se499.kayaanbackend.service;

import se499.kayaanbackend.DTO.FlashcardRequestDTO;
import se499.kayaanbackend.DTO.FlashcardResponseDTO;

import java.util.List;

public interface FlashcardService {
    FlashcardResponseDTO createFlashcard(FlashcardRequestDTO dto, String username);
    List<FlashcardResponseDTO> getAllFlashcardsForUser(String username);
    FlashcardResponseDTO getFlashcardById(Long id, String username);
    void deleteFlashcard(Long id, String username);
}

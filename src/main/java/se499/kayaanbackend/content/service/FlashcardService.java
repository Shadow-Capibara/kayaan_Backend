package se499.kayaanbackend.content.service;

import se499.kayaanbackend.content.dto.FlashcardDto;

public interface FlashcardService {
    FlashcardDto createFlashcard(Long userId, FlashcardDto dto);
    FlashcardDto getFlashcard(Long id);
    FlashcardDto updateFlashcard(Long id, FlashcardDto dto);
    void deleteFlashcard(Long id);
    void restoreFlashcard(Long id);
}

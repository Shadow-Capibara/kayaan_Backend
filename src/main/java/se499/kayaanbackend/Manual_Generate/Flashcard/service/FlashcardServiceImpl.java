package se499.kayaanbackend.Manual_Generate.Flashcard.service;

import java.util.List;

import org.springframework.stereotype.Service;

import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardRequestDTO;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardResponseDTO;
import se499.kayaanbackend.Manual_Generate.Flashcard.repository.FlashcardRepository;
import se499.kayaanbackend.Manual_Generate.Group.entity.Group;
import se499.kayaanbackend.Manual_Generate.Group.repository.GroupRepository;

@Service
public class FlashcardServiceImpl implements FlashcardService {
    
    private final FlashcardRepository flashcardRepository;
    
    public FlashcardServiceImpl(FlashcardRepository flashcardRepository) {
        this.flashcardRepository = flashcardRepository;
    }
    
    @Override
    public FlashcardResponseDTO createFlashcard(FlashcardRequestDTO dto, String username) {
        // Stub implementation - return null for now
        return null;

    }
    
    @Override
    public List<FlashcardResponseDTO> getAllFlashcardsForUser(String username) {
        // Stub implementation - return empty list
        return List.of();

    }
    
    @Override
    public FlashcardResponseDTO getFlashcardById(Long id, String username) {
        // Stub implementation - return null for now
        return null;


    }
    
    @Override
    public void deleteFlashcard(Long id, String username) {
        // Stub implementation - do nothing
    }
}

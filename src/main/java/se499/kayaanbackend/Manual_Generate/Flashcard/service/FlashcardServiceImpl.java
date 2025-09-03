package se499.kayaanbackend.Manual_Generate.Flashcard.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardRequestDTO;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardResponseDTO;
import se499.kayaanbackend.Manual_Generate.entity.ManualFlashcard;
import se499.kayaanbackend.Manual_Generate.repository.ManualFlashcardRepository;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.security.user.UserRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class FlashcardServiceImpl implements FlashcardService {
    
    private final ManualFlashcardRepository flashcardRepository;
    private final UserRepository userRepository;
    
    @Override
    public FlashcardResponseDTO createFlashcard(FlashcardRequestDTO dto, String username) {
        try {
            log.info("Creating flashcard for user: {}", username);
            
            // Find user
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));
            
            // Create flashcard
            ManualFlashcard flashcard = ManualFlashcard.builder()
                .user(user)
                .frontText(dto.getFrontText())
                .backText(dto.getBackText())
                .subject(dto.getSubject())
                .difficulty(dto.getDifficulty())
                .tags(dto.getTags() != null ? String.join(",", dto.getTags()) : null) // Convert List<String> to String
                .build();
                
            ManualFlashcard savedFlashcard = flashcardRepository.save(flashcard);
            log.info("Flashcard created with ID: {}", savedFlashcard.getId());
            
            return mapToResponseDTO(savedFlashcard);
            
        } catch (Exception e) {
            log.error("Error creating flashcard for user: {}", username, e);
            throw new RuntimeException("Failed to create flashcard: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<FlashcardResponseDTO> getAllFlashcardsForUser(String username) {
        try {
            log.info("Getting all flashcards for user: {}", username);
            
            List<ManualFlashcard> flashcards = flashcardRepository.findByUsernameAndNotDeleted(username);
            
            return flashcards.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error getting flashcards for user: {}", username, e);
            throw new RuntimeException("Failed to get flashcards: " + e.getMessage());
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public FlashcardResponseDTO getFlashcardById(Long id, String username) {
        try {
            log.info("Getting flashcard {} for user: {}", id, username);
            
            ManualFlashcard flashcard = flashcardRepository.findByIdAndUsernameAndNotDeleted(id, username)
                .orElseThrow(() -> new RuntimeException("Flashcard not found or access denied: " + id));
            
            return mapToResponseDTO(flashcard);
            
        } catch (Exception e) {
            log.error("Error getting flashcard {} for user: {}", id, username, e);
            throw new RuntimeException("Failed to get flashcard: " + e.getMessage());
        }
    }
    
    @Override
    public void deleteFlashcard(Long id, String username) {
        try {
            log.info("Deleting flashcard {} for user: {}", id, username);
            
            ManualFlashcard flashcard = flashcardRepository.findByIdAndUsernameAndNotDeleted(id, username)
                .orElseThrow(() -> new RuntimeException("Flashcard not found or access denied: " + id));
            
            // Soft delete
            flashcard.markAsDeleted();
            flashcardRepository.save(flashcard);
            
            log.info("Flashcard {} deleted successfully", id);
            
        } catch (Exception e) {
            log.error("Error deleting flashcard {} for user: {}", id, username, e);
            throw new RuntimeException("Failed to delete flashcard: " + e.getMessage());
        }
    }
    
    private FlashcardResponseDTO mapToResponseDTO(ManualFlashcard flashcard) {
        return FlashcardResponseDTO.builder()
            .id(flashcard.getId())
            .createdByUsername(flashcard.getUser().getUsername())
            .frontText(flashcard.getFrontText())
            .backText(flashcard.getBackText())
            .subject(flashcard.getSubject())
            .difficulty(flashcard.getDifficulty())
            .tags(flashcard.getTags() != null && !flashcard.getTags().trim().isEmpty()
                ? Arrays.asList(flashcard.getTags().split(","))  // Convert String to List<String>
                : List.of())
            .createdAt(flashcard.getCreatedAt())
            .updatedAt(flashcard.getUpdatedAt())
            .build();
    }
}

package se499.kayaanbackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.DTO.FlashcardRequestDTO;
import se499.kayaanbackend.DTO.FlashcardResponseDTO;
import se499.kayaanbackend.entity.Flashcard;
import se499.kayaanbackend.repository.FlashcardRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FlashcardServiceImpl implements FlashcardService {
    private final FlashcardRepository flashcardRepository;

    @Override
    public FlashcardResponseDTO createFlashcard(FlashcardRequestDTO dto, String username) {
        Flashcard card = Flashcard.builder()
                .frontText(dto.getFrontText())
                .backText(dto.getBackText())
                .subject(dto.getSubject())
                .difficulty(dto.getDifficulty())
                .tags(dto.getTags())
                .createdByUsername(username)
                .build();

        Flashcard saved = flashcardRepository.save(card);
        return FlashcardResponseDTO.builder()
                .id(saved.getId())
                .createdByUsername(saved.getCreatedByUsername())
                .frontText(saved.getFrontText())
                .backText(saved.getBackText())
                .subject(saved.getSubject())
                .difficulty(saved.getDifficulty())
                .tags(saved.getTags())
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardResponseDTO> getAllFlashcardsForUser(String username) {
        return flashcardRepository.findByCreatedByUsername(username)
                .stream()
                .map(f -> FlashcardResponseDTO.builder()
                        .id(f.getId())
                        .createdByUsername(f.getCreatedByUsername())
                        .frontText(f.getFrontText())
                        .backText(f.getBackText())
                        .subject(f.getSubject())
                        .difficulty(f.getDifficulty())
                        .tags(f.getTags())
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FlashcardResponseDTO getFlashcardById(Long id, String username) {
        Flashcard card = flashcardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flashcard not found: " + id));
        if (!card.getCreatedByUsername().equals(username)) {
            throw new SecurityException("Not authorized");
        }
        return FlashcardResponseDTO.builder()
                .id(card.getId())
                .createdByUsername(card.getCreatedByUsername())
                .frontText(card.getFrontText())
                .backText(card.getBackText())
                .subject(card.getSubject())
                .difficulty(card.getDifficulty())
                .tags(card.getTags())
                .build();
    }

    @Override
    public void deleteFlashcard(Long id, String username) {
        Flashcard card = flashcardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flashcard not found: " + id));
        if (!card.getCreatedByUsername().equals(username)) {
            throw new SecurityException("Not authorized");
        }
        flashcardRepository.delete(card);
    }
}

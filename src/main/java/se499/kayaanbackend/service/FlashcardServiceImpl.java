package se499.kayaanbackend.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.DTO.FlashcardRequestDTO;
import se499.kayaanbackend.DTO.FlashcardResponseDTO;
import se499.kayaanbackend.entity.ContentInformation;
import se499.kayaanbackend.entity.Flashcard;
import se499.kayaanbackend.repository.ContentInformationRepository;
import se499.kayaanbackend.repository.FlashcardRepository;
import se499.kayaanbackend.security.user.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FlashcardServiceImpl implements FlashcardService {
    private final FlashcardRepository flashcardRepository;
    private final ContentInformationRepository contentInformationRepository;
    private final UserService userService;

    @Override
    public FlashcardResponseDTO createFlashcard(FlashcardRequestDTO dto, String username) {
        var user = userService.findByUsername(username);

        ContentInformation content = ContentInformation.builder()
                .contentType(ContentInformation.ContentType.FLASHCARD)
                .contentSubject(dto.getSubject())
                .contentTitle(dto.getFrontText())
                .tag(dto.getTags() != null && !dto.getTags().isEmpty() ? dto.getTags().get(0) : null)
                .difficulty(ContentInformation.Difficulty.valueOf(dto.getDifficulty().toUpperCase()))
                .user(user)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        content = contentInformationRepository.save(content);

        Flashcard card = Flashcard.builder()
                .frontText(dto.getFrontText())
                .backText(dto.getBackText())
                .contentInformation(content)
                .createdAt(java.time.LocalDateTime.now())
                .updatedAt(java.time.LocalDateTime.now())
                .build();

        Flashcard saved = flashcardRepository.save(card);
        return FlashcardResponseDTO.builder()
                .id(saved.getId())
                .createdByUsername(username)
                .frontText(saved.getFrontText())
                .backText(saved.getBackText())
                .subject(content.getContentSubject())
                .difficulty(content.getDifficulty().name())
                .tags(content.getTag() == null ? null : java.util.List.of(content.getTag()))
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardResponseDTO> getAllFlashcardsForUser(String username) {
        return flashcardRepository.findByContentInformation_User_Username(username)
                .stream()
                .map(f -> FlashcardResponseDTO.builder()
                        .id(f.getId())
                        .createdByUsername(username)
                        .frontText(f.getFrontText())
                        .backText(f.getBackText())
                        .subject(f.getContentInformation().getContentSubject())
                        .difficulty(f.getContentInformation().getDifficulty().name())
                        .tags(f.getContentInformation().getTag() == null ? null : java.util.List.of(f.getContentInformation().getTag()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public FlashcardResponseDTO getFlashcardById(Long id, String username) {
        Flashcard card = flashcardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flashcard not found: " + id));
        if (!card.getContentInformation().getUser().getUsername().equals(username)) {
            throw new SecurityException("Not authorized");
        }
        return FlashcardResponseDTO.builder()
                .id(card.getId())
                .createdByUsername(username)
                .frontText(card.getFrontText())
                .backText(card.getBackText())
                .subject(card.getContentInformation().getContentSubject())
                .difficulty(card.getContentInformation().getDifficulty().name())
                .tags(card.getContentInformation().getTag() == null ? null : java.util.List.of(card.getContentInformation().getTag()))
                .build();
    }

    @Override
    public void deleteFlashcard(Long id, String username) {
        Flashcard card = flashcardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flashcard not found: " + id));
        if (!card.getContentInformation().getUser().getUsername().equals(username)) {
            throw new SecurityException("Not authorized");
        }
        flashcardRepository.delete(card);
    }
}

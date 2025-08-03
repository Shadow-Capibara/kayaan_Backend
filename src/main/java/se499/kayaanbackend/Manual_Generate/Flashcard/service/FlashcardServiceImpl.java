package se499.kayaanbackend.Manual_Generate.Flashcard.service;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardRequestDTO;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardResponseDTO;
import se499.kayaanbackend.Manual_Generate.Flashcard.repository.FlashcardRepository;
import se499.kayaanbackend.Manual_Generate.Group.entity.Group;
import se499.kayaanbackend.Manual_Generate.Group.repository.GroupRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FlashcardServiceImpl implements FlashcardService {
    private final FlashcardRepository flashcardRepository;
    private final GroupRepository groupRepository;

    @Override
    public FlashcardResponseDTO createFlashcard(FlashcardRequestDTO dto, String username) {
        List<Group> groups = dto.getGroupIds() == null ? java.util.Collections.emptyList() :
                groupRepository.findAllById(dto.getGroupIds());

        Flashcard card = Flashcard.builder()
                .frontText(dto.getFrontText())
                .backText(dto.getBackText())
                .frontImageUrl(dto.getFrontImageUrl())
                .backImageUrl(dto.getBackImageUrl())
                .subject(dto.getSubject())
                .difficulty(dto.getDifficulty())
                .category(dto.getCategory())
                .tags(dto.getTags())
                .createdByUsername(username)
                .sharedGroups(groups)
                .build();

        Flashcard saved = flashcardRepository.save(card);
        return FlashcardResponseDTO.builder()
                .id(saved.getId())
                .createdByUsername(saved.getCreatedByUsername())
                .frontText(saved.getFrontText())
                .backText(saved.getBackText())
                .frontImageUrl(saved.getFrontImageUrl())
                .backImageUrl(saved.getBackImageUrl())
                .subject(saved.getSubject())
                .difficulty(saved.getDifficulty())
                .category(saved.getCategory())
                .tags(saved.getTags())
                .groupIds(saved.getSharedGroups().stream().map(Group::getId).collect(Collectors.toList()))
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
                        .frontImageUrl(f.getFrontImageUrl())
                        .backImageUrl(f.getBackImageUrl())
                        .subject(f.getSubject())
                        .difficulty(f.getDifficulty())
                        .category(f.getCategory())
                        .tags(f.getTags())
                        .groupIds(f.getSharedGroups().stream().map(Group::getId).collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardResponseDTO> getFlashcardsByCategory(String username, String category) {
        return flashcardRepository.findByCreatedByUsernameAndCategory(username, category)
                .stream()
                .map(f -> FlashcardResponseDTO.builder()
                        .id(f.getId())
                        .createdByUsername(f.getCreatedByUsername())
                        .frontText(f.getFrontText())
                        .backText(f.getBackText())
                        .frontImageUrl(f.getFrontImageUrl())
                        .backImageUrl(f.getBackImageUrl())
                        .subject(f.getSubject())
                        .difficulty(f.getDifficulty())
                        .category(f.getCategory())
                        .tags(f.getTags())
                        .groupIds(f.getSharedGroups().stream().map(Group::getId).collect(Collectors.toList()))
                        .build())
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<FlashcardResponseDTO> getFlashcardsBySubject(String username, String subject) {
        return flashcardRepository.findByCreatedByUsernameAndSubject(username, subject)
                .stream()
                .map(f -> FlashcardResponseDTO.builder()
                        .id(f.getId())
                        .createdByUsername(f.getCreatedByUsername())
                        .frontText(f.getFrontText())
                        .backText(f.getBackText())
                        .frontImageUrl(f.getFrontImageUrl())
                        .backImageUrl(f.getBackImageUrl())
                        .subject(f.getSubject())
                        .difficulty(f.getDifficulty())
                        .category(f.getCategory())
                        .tags(f.getTags())
                        .groupIds(f.getSharedGroups().stream().map(Group::getId).collect(Collectors.toList()))
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
                .frontImageUrl(card.getFrontImageUrl())
                .backImageUrl(card.getBackImageUrl())
                .subject(card.getSubject())
                .difficulty(card.getDifficulty())
                .category(card.getCategory())
                .tags(card.getTags())
                .groupIds(card.getSharedGroups().stream().map(Group::getId).collect(Collectors.toList()))
                .build();
    }

    @Override
    public FlashcardResponseDTO updateFlashcard(Long id, FlashcardRequestDTO dto, String username) {
        Flashcard card = flashcardRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Flashcard not found: " + id));
        if (!card.getCreatedByUsername().equals(username)) {
            throw new SecurityException("Not authorized");
        }
        card.setFrontText(dto.getFrontText());
        card.setBackText(dto.getBackText());
        card.setFrontImageUrl(dto.getFrontImageUrl());
        card.setBackImageUrl(dto.getBackImageUrl());
        card.setSubject(dto.getSubject());
        card.setDifficulty(dto.getDifficulty());
        card.setCategory(dto.getCategory());
        card.setTags(dto.getTags());
        List<Group> groups = dto.getGroupIds() == null ? java.util.Collections.emptyList() :
                groupRepository.findAllById(dto.getGroupIds());
        card.setSharedGroups(groups);
        Flashcard saved = flashcardRepository.save(card);
        return FlashcardResponseDTO.builder()
                .id(saved.getId())
                .createdByUsername(saved.getCreatedByUsername())
                .frontText(saved.getFrontText())
                .backText(saved.getBackText())
                .frontImageUrl(saved.getFrontImageUrl())
                .backImageUrl(saved.getBackImageUrl())
                .subject(saved.getSubject())
                .difficulty(saved.getDifficulty())
                .category(saved.getCategory())
                .tags(saved.getTags())
                .groupIds(saved.getSharedGroups().stream().map(Group::getId).collect(Collectors.toList()))
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

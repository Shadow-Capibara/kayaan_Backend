package se499.kayaanbackend.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se499.kayaanbackend.content.dto.FlashcardDto;
import se499.kayaanbackend.content.dto.FlashcardImageDto;
import se499.kayaanbackend.content.entity.ContentInformation;
import se499.kayaanbackend.content.entity.FlashcardImage;
import se499.kayaanbackend.content.entity.FlashcardInformation;
import se499.kayaanbackend.content.enums.ContentType;
import se499.kayaanbackend.content.repository.ContentInformationRepository;
import se499.kayaanbackend.content.repository.FlashcardInformationRepository;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class FlashcardServiceImpl implements FlashcardService {

    private final ContentInformationRepository contentRepository;
    private final FlashcardInformationRepository flashcardRepository;

    @Override
    public FlashcardDto createFlashcard(Long userId, FlashcardDto dto) {
        ContentInformation content = ContentInformation.builder()
                .userId(userId)
                .contentType(ContentType.FLASHCARD)
                .build();
        contentRepository.save(content);

        FlashcardInformation flashcard = FlashcardInformation.builder()
                .contentInformation(content)
                .title(dto.getTitle())
                .build();

        if (dto.getImages() != null) {
            flashcard.setImages(dto.getImages().stream()
                    .map(i -> FlashcardImage.builder()
                            .flashcard(flashcard)
                            .imageUrl(i.getImageUrl())
                            .build())
                    .collect(Collectors.toList()));
        }

        FlashcardInformation saved = flashcardRepository.save(flashcard);
        dto.setFlashcardId(saved.getFlashcardId());
        dto.setContentId(content.getContentId());
        return dto;
    }

    @Override
    @Transactional(readOnly = true)
    public FlashcardDto getFlashcard(Long id) {
        FlashcardInformation flashcard = flashcardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flashcard not found"));
        return toDto(flashcard);
    }

    @Override
    public FlashcardDto updateFlashcard(Long id, FlashcardDto dto) {
        FlashcardInformation flashcard = flashcardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flashcard not found"));
        flashcard.setTitle(dto.getTitle());
        flashcard.getImages().clear();
        if (dto.getImages() != null) {
            flashcard.getImages().addAll(dto.getImages().stream()
                    .map(i -> FlashcardImage.builder()
                            .flashcard(flashcard)
                            .imageUrl(i.getImageUrl())
                            .build())
                    .collect(Collectors.toList()));
        }
        return toDto(flashcardRepository.save(flashcard));
    }

    @Override
    public void deleteFlashcard(Long id) {
        FlashcardInformation flashcard = flashcardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flashcard not found"));
        LocalDateTime now = LocalDateTime.now();
        flashcard.setDeletedAt(now);
        flashcard.getContentInformation().setDeletedAt(now);
        flashcardRepository.save(flashcard);
    }

    @Override
    public void restoreFlashcard(Long id) {
        FlashcardInformation flashcard = flashcardRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Flashcard not found"));
        flashcard.setDeletedAt(null);
        flashcard.getContentInformation().setDeletedAt(null);
        flashcardRepository.save(flashcard);
    }

    private FlashcardDto toDto(FlashcardInformation flashcard) {
        FlashcardDto dto = new FlashcardDto();
        dto.setFlashcardId(flashcard.getFlashcardId());
        dto.setContentId(flashcard.getContentInformation().getContentId());
        dto.setTitle(flashcard.getTitle());
        dto.setImages(
                flashcard.getImages().stream()
                        .map(i -> {
                            FlashcardImageDto idto = new FlashcardImageDto();
                            idto.setImageId(i.getImageId());
                            idto.setImageUrl(i.getImageUrl());
                            return idto;
                        }).collect(Collectors.toList())
        );
        return dto;
    }
}

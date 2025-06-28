package se499.kayaanbackend.content.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import se499.kayaanbackend.content.dto.ContentDto;
import se499.kayaanbackend.content.entity.ContentInformation;
import se499.kayaanbackend.content.repository.ContentInformationRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ContentInformationServiceImpl implements ContentInformationService {

    private final ContentInformationRepository repository;

    @Override
    @Transactional(readOnly = true)
    public ContentDto getContent(Long id) {
        ContentInformation entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        return toDto(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ContentDto> getUserContents(Long userId) {
        return repository.findByUserIdAndDeletedAtIsNull(userId)
                .stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public void softDelete(Long id) {
        ContentInformation entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        entity.setDeletedAt(LocalDateTime.now());
        repository.save(entity);
    }

    @Override
    public void restore(Long id) {
        ContentInformation entity = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Content not found"));
        entity.setDeletedAt(null);
        repository.save(entity);
    }

    private ContentDto toDto(ContentInformation entity) {
        ContentDto dto = new ContentDto();
        dto.setContentId(entity.getContentId());
        dto.setUserId(entity.getUserId());
        dto.setContentType(entity.getContentType());
        dto.setCreatedAt(entity.getCreatedAt());
        dto.setUpdatedAt(entity.getUpdatedAt());
        dto.setDeletedAt(entity.getDeletedAt());
        return dto;
    }
}

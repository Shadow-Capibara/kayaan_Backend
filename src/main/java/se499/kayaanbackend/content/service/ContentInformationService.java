package se499.kayaanbackend.content.service;

import se499.kayaanbackend.content.dto.ContentDto;

import java.util.List;

public interface ContentInformationService {
    ContentDto getContent(Long id);
    List<ContentDto> getUserContents(Long userId);
    void softDelete(Long id);
    void restore(Long id);
}

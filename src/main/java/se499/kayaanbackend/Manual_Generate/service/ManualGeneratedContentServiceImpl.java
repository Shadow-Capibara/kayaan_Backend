package se499.kayaanbackend.Manual_Generate.service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.AI_Generate.entity.ContentType;
import se499.kayaanbackend.Manual_Generate.dto.CreateManualContentDTO;
import se499.kayaanbackend.Manual_Generate.dto.ManualGeneratedContentDTO;
import se499.kayaanbackend.Manual_Generate.entity.ManualGeneratedContent;
import se499.kayaanbackend.Manual_Generate.exception.ContentValidationException;
import se499.kayaanbackend.Manual_Generate.exception.ManualGenerationException;
import se499.kayaanbackend.Manual_Generate.repository.ManualGeneratedContentRepository;
import se499.kayaanbackend.security.user.User;
import se499.kayaanbackend.security.user.UserRepository;

/**
 * Implementation of ManualGeneratedContentService
 * Handles JSON-based manual content operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ManualGeneratedContentServiceImpl implements ManualGeneratedContentService {
    
    private final ManualGeneratedContentRepository repository;
    private final UserRepository userRepository;
    private final ObjectMapper objectMapper;
    
    @Override
    public ManualGeneratedContentDTO createContent(CreateManualContentDTO dto, String username) {
        try {
            log.debug("Creating manual content for user: {} with type: {}", username, dto.getContentType());
            
            // Validate user
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ManualGenerationException("User not found: " + username));
            
            // Validate content type
            ContentType contentType;
            try {
                contentType = ContentType.fromString(dto.getContentType());
                log.debug("Content type validation passed: {}", contentType);
            } catch (IllegalArgumentException e) {
                log.error("Invalid content type: {}", dto.getContentType());
                throw new ContentValidationException("Invalid content type: " + dto.getContentType());
            }
            
            // Validate JSON content
            boolean isValid = validateJsonContent(dto.getContentType(), dto.getContentData());
            if (!isValid) {
                log.error("JSON validation failed for content type: {}", dto.getContentType());
                throw new ContentValidationException("Invalid JSON content structure for type: " + dto.getContentType());
            }
            
            // Create entity
            ManualGeneratedContent content = ManualGeneratedContent.builder()
                .user(user)
                .contentTitle(dto.getContentTitle())
                .contentType(contentType)
                .contentData(dto.getContentData())
                .contentVersion(dto.getContentVersion())
                .subject(dto.getSubject())
                .difficulty(dto.getDifficulty())
                .tags(dto.getTags() != null ? String.join(",", dto.getTags()) : null)
                .isSaved(dto.getIsSaved())
                .build();
            
            ManualGeneratedContent savedContent = repository.save(content);
            log.info("Manual content created successfully with ID: {} for user: {}", savedContent.getId(), username);
            
            return mapToDTO(savedContent);
            
        } catch (Exception e) {
            log.error("Error creating manual content for user: {} - {}", username, e.getMessage());
            throw new ManualGenerationException("Failed to create manual content: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ManualGeneratedContentDTO getContentById(Long id, String username) {
        try {
            log.debug("Getting manual content {} for user: {}", id, username);
            
            ManualGeneratedContent content = repository.findByIdAndUsernameAndNotDeleted(id, username)
                .orElseThrow(() -> new ManualGenerationException("Content not found or access denied: " + id));
            
            return mapToDTO(content);
            
        } catch (Exception e) {
            log.error("Error getting manual content {} for user: {} - {}", id, username, e.getMessage());
            throw new ManualGenerationException("Failed to get manual content: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ManualGeneratedContentDTO updateContent(Long id, CreateManualContentDTO dto, String username) {
        try {
            log.debug("Updating manual content {} for user: {}", id, username);
            
            ManualGeneratedContent content = repository.findByIdAndUsernameAndNotDeleted(id, username)
                .orElseThrow(() -> new ManualGenerationException("Content not found or access denied: " + id));
            
            // Validate content type if changed
            if (dto.getContentType() != null && !dto.getContentType().equals(content.getContentType().getValue())) {
                try {
                    ContentType newContentType = ContentType.fromString(dto.getContentType());
                    content.setContentType(newContentType);
                    log.debug("Content type changed to: {}", newContentType);
                } catch (IllegalArgumentException e) {
                    log.error("Invalid content type for update: {}", dto.getContentType());
                    throw new ContentValidationException("Invalid content type: " + dto.getContentType());
                }
            }
            
            // Update fields
            if (dto.getContentTitle() != null) content.setContentTitle(dto.getContentTitle());
            if (dto.getContentData() != null) {
                boolean isValid = validateJsonContent(content.getContentType().getValue(), dto.getContentData());
                if (!isValid) {
                    log.error("JSON validation failed for update, content type: {}", content.getContentType().getValue());
                    throw new ContentValidationException("Invalid JSON content structure");
                }
                content.setContentData(dto.getContentData());
                content.setContentVersion(content.getContentVersion() + 1);
            }
            if (dto.getSubject() != null) content.setSubject(dto.getSubject());
            if (dto.getDifficulty() != null) content.setDifficulty(dto.getDifficulty());
            if (dto.getTags() != null) content.setTags(String.join(",", dto.getTags()));
            
            ManualGeneratedContent updatedContent = repository.save(content);
            log.info("Manual content updated successfully: {} for user: {}", id, username);
            
            return mapToDTO(updatedContent);
            
        } catch (Exception e) {
            log.error("Error updating manual content {} for user: {} - {}", id, username, e.getMessage());
            throw new ManualGenerationException("Failed to update manual content: " + e.getMessage(), e);
        }
    }
    
    @Override
    public void deleteContent(Long id, String username) {
        try {
            log.debug("Deleting manual content {} for user: {}", id, username);
            
            ManualGeneratedContent content = repository.findByIdAndUsernameAndNotDeleted(id, username)
                .orElseThrow(() -> new ManualGenerationException("Content not found or access denied: " + id));
            
            content.softDelete();
            repository.save(content);
            
            log.info("Manual content deleted successfully: {} for user: {}", id, username);
            
        } catch (Exception e) {
            log.error("Error deleting manual content {} for user: {} - {}", id, username, e.getMessage());
            throw new ManualGenerationException("Failed to delete manual content: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ManualGeneratedContentDTO> getAllContentForUser(String username) {
        try {
            log.debug("Getting all manual content for user: {}", username);
            
            List<ManualGeneratedContent> contents = repository.findByUsernameAndNotDeleted(username);
            log.debug("Found {} content items for user: {}", contents.size(), username);
            
            return contents.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error getting all manual content for user: {} - {}", username, e.getMessage());
            throw new ManualGenerationException("Failed to get manual content: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ManualGeneratedContentDTO> getContentByTypeForUser(String username, ContentType contentType) {
        try {
            log.debug("Getting manual content of type {} for user: {}", contentType, username);
            
            List<ManualGeneratedContent> contents = repository.findByUsernameAndContentTypeAndNotDeleted(username, contentType);
            log.debug("Found {} content items of type {} for user: {}", contents.size(), contentType, username);
            
            return contents.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error getting manual content by type for user: {} - {}", username, e.getMessage());
            throw new ManualGenerationException("Failed to get manual content: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public List<ManualGeneratedContentDTO> searchContentByTitle(String username, String searchTerm) {
        try {
            log.debug("Searching manual content for user: {} with term: {}", username, searchTerm);
            
            List<ManualGeneratedContent> contents = repository.searchByTitleAndUsername(username, searchTerm);
            log.debug("Found {} content items matching '{}' for user: {}", contents.size(), searchTerm, username);
            
            return contents.stream()
                .map(this::mapToDTO)
                .collect(Collectors.toList());
                
        } catch (Exception e) {
            log.error("Error searching manual content for user: {} - {}", username, e.getMessage());
            throw new ManualGenerationException("Failed to search manual content: " + e.getMessage(), e);
        }
    }
    
    @Override
    @Transactional(readOnly = true)
    public ContentStatistics getContentStatistics(String username) {
        try {
            log.debug("Getting content statistics for user: {}", username);
            
            long totalContent = repository.countByUsernameAndNotDeleted(username);
            long quizCount = repository.countByUsernameAndContentTypeAndNotDeleted(username, ContentType.QUIZ);
            long noteCount = repository.countByUsernameAndContentTypeAndNotDeleted(username, ContentType.NOTE);
            long flashcardCount = repository.countByUsernameAndContentTypeAndNotDeleted(username, ContentType.FLASHCARD);
            
            log.debug("Content statistics for user {}: total={}, quiz={}, note={}, flashcard={}", 
                     username, totalContent, quizCount, noteCount, flashcardCount);
            
            return new ContentStatistics(totalContent, quizCount, noteCount, flashcardCount);
            
        } catch (Exception e) {
            log.error("Error getting content statistics for user: {} - {}", username, e.getMessage());
            throw new ManualGenerationException("Failed to get content statistics: " + e.getMessage(), e);
        }
    }
    
    @Override
    public ManualGeneratedContentDTO convertLegacyContent(String contentType, Object legacyContent, String username) {
        // TODO: Implement legacy content conversion
        throw new UnsupportedOperationException("Legacy content conversion not yet implemented");
    }
    
    @Override
    public boolean validateJsonContent(String contentType, String jsonContent) {
        try {
            log.info("=== Starting JSON validation ===");
            log.info("Validating JSON content for type: {}", contentType);
            log.info("JSON content received: {}", jsonContent);
            
            if (jsonContent == null || jsonContent.trim().isEmpty()) {
                log.warn("JSON content is null or empty");
                return false;
            }
            
            JsonNode jsonNode = objectMapper.readTree(jsonContent);
            log.info("JSON parsed successfully: {}", jsonNode.toString());
            
            // Basic structure validation - more flexible approach
            if (!jsonNode.has("type")) {
                log.debug("JSON missing 'type' field, but continuing validation");
            } else {
                String type = jsonNode.get("type").asText();
                log.info("JSON type field: '{}', expected: '{}'", type, contentType);
                if (!type.equalsIgnoreCase(contentType)) {
                    log.debug("JSON type '{}' does not match content type '{}', but continuing validation", type, contentType);
                }
            }
            
            // Content-specific validation with more flexible rules
            boolean isValid = false;
            switch (contentType.toLowerCase()) {
                case "quiz":
                    log.info("Validating quiz JSON structure");
                    isValid = validateQuizJson(jsonNode);
                    break;
                case "note":
                    log.info("Validating note JSON structure");
                    isValid = validateNoteJson(jsonNode);
                    break;
                case "flashcard":
                    log.info("Validating flashcard JSON structure");
                    isValid = validateFlashcardJson(jsonNode);
                    break;
                default:
                    log.warn("Unknown content type: {}", contentType);
                    return false;
            }
            
            log.info("JSON validation result for {}: {}", contentType, isValid);
            return isValid;
            
        } catch (Exception e) {
            log.error("JSON validation failed for content type {}: {}", contentType, e.getMessage());
            log.error("Exception details: ", e);
            return false;
        }
    }
    
    private boolean validateQuizJson(JsonNode jsonNode) {
        log.debug("Validating quiz JSON structure");
        
        // More flexible validation - check if it has at least some quiz-like structure
        boolean hasQuestions = jsonNode.has("questions") && jsonNode.get("questions").isArray();
        boolean hasQuizFields = jsonNode.has("quiz") || jsonNode.has("question") || jsonNode.has("options");
        
        if (!hasQuestions && !hasQuizFields) {
            log.debug("Quiz JSON missing basic quiz structure");
            return false;
        }
        
        // If it has questions array, validate it
        if (hasQuestions) {
            JsonNode questions = jsonNode.get("questions");
            if (questions.size() == 0) {
                log.debug("Quiz JSON 'questions' array is empty");
                return false;
            }
            
            // Validate each question structure
            for (int i = 0; i < questions.size(); i++) {
                JsonNode question = questions.get(i);
                log.debug("Validating question {}: {}", i + 1, question.toString());
                
                // Check required fields
                if (!question.has("question") && !question.has("text")) {
                    log.debug("Question {} missing question text", i + 1);
                    return false;
                }
                
                // Check question type if present
                if (question.has("type")) {
                    String questionType = question.get("type").asText().toLowerCase();
                    log.debug("Question {} type: {}", i + 1, questionType);
                    
                    // Validate based on question type
                    if ("open-ended".equals(questionType) || "open_ended".equals(questionType)) {
                        // Open-ended questions should not have options or have empty/null options
                        if (question.has("options")) {
                            JsonNode options = question.get("options");
                            if (options.isArray() && options.size() > 0) {
                                log.debug("Question {} is open-ended but has non-empty options", i + 1);
                                // This is a warning but not a validation failure
                                log.warn("Open-ended question {} has options - this may cause frontend confusion", i + 1);
                            }
                        }
                    } else if ("multiple-choice".equals(questionType) || "multiple_choice".equals(questionType)) {
                        // Multiple choice questions should have options
                        if (!question.has("options") || !question.get("options").isArray() || question.get("options").size() == 0) {
                            log.debug("Question {} is multiple-choice but missing options", i + 1);
                            return false;
                        }
                    }
                }
            }
        }
        
        log.debug("Quiz JSON validation successful");
        return true;
    }
    
    private boolean validateNoteJson(JsonNode jsonNode) {
        log.info("=== Validating note JSON structure ===");
        log.info("Note JSON node: {}", jsonNode.toString());
        
        // More flexible validation for notes
        boolean hasContent = jsonNode.has("content") && jsonNode.get("content").isArray();
        boolean hasText = jsonNode.has("text") || jsonNode.has("description") || jsonNode.has("body");
        boolean hasSections = jsonNode.has("sections") && jsonNode.get("sections").isArray();
        
        log.info("Note validation checks: hasContent={}, hasText={}, hasSections={}", hasContent, hasText, hasSections);
        
        if (!hasContent && !hasText && !hasSections) {
            log.error("Note JSON missing basic note structure - all validation checks failed");
            return false;
        }
        
        // If it has content array, validate it
        if (hasContent) {
            JsonNode content = jsonNode.get("content");
            log.info("Note content array size: {}", content.size());
            if (content.size() == 0) {
                log.error("Note JSON 'content' array is empty");
                return false;
            }
            
            // Log first content item for debugging
            if (content.size() > 0) {
                JsonNode firstItem = content.get(0);
                log.info("First content item: {}", firstItem.toString());
                log.info("Has feature: {}, Has description: {}", 
                    firstItem.has("feature"), firstItem.has("description"));
            }
        }
        
        log.info("Note JSON validation successful");
        return true;
    }
    
    private boolean validateFlashcardJson(JsonNode jsonNode) {
        log.debug("Validating flashcard JSON structure");
        
        // More flexible validation for flashcards
        boolean hasFlashcards = jsonNode.has("flashcards") && jsonNode.get("flashcards").isArray();
        boolean hasCards = jsonNode.has("cards") && jsonNode.get("cards").isArray();
        boolean hasQuestionAnswer = jsonNode.has("question") && jsonNode.has("answer");
        
        if (!hasFlashcards && !hasCards && !hasQuestionAnswer) {
            log.debug("Flashcard JSON missing basic flashcard structure");
            return false;
        }
        
        // If it has flashcards array, validate it
        if (hasFlashcards) {
            JsonNode flashcards = jsonNode.get("flashcards");
            if (flashcards.size() == 0) {
                log.debug("Flashcard JSON 'flashcards' array is empty");
                return false;
            }
            
            // Validate first flashcard as sample
            JsonNode firstCard = flashcards.get(0);
            if (!firstCard.has("question") && !firstCard.has("front")) {
                log.debug("Flashcard missing question/front field");
                return false;
            }
            if (!firstCard.has("answer") && !firstCard.has("back")) {
                log.debug("Flashcard missing answer/back field");
                return false;
            }
        }
        
        log.debug("Flashcard JSON validation successful");
        return true;
    }
    
    private ManualGeneratedContentDTO mapToDTO(ManualGeneratedContent content) {
        List<String> tagsList = null;
        if (content.getTags() != null && !content.getTags().trim().isEmpty()) {
            tagsList = Arrays.asList(content.getTags().split(","));
        }
        
        return ManualGeneratedContentDTO.builder()
            .id(content.getId())
            .userId(content.getUser().getId().longValue())
            .username(content.getUser().getUsername())
            .contentTitle(content.getContentTitle())
            .contentType(content.getContentType().getValue())
            .contentData(content.getContentData())
            .contentVersion(content.getContentVersion())
            .subject(content.getSubject())
            .difficulty(content.getDifficulty())
            .tags(tagsList)
            .isSaved(content.getIsSaved())
            .createdAt(content.getCreatedAt())
            .updatedAt(content.getUpdatedAt())
            .contentSize(content.getContentData() != null ? (long) content.getContentData().length() : 0L)
            .build();
    }
}

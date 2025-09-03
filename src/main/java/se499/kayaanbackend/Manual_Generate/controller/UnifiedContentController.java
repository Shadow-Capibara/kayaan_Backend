package se499.kayaanbackend.Manual_Generate.controller;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.AI_Generate.dto.AIGeneratedContentDTO;
import se499.kayaanbackend.AI_Generate.service.AIGenerationService;
import se499.kayaanbackend.Manual_Generate.dto.ManualGeneratedContentDTO;
import se499.kayaanbackend.Manual_Generate.dto.UnifiedContentDTO;
import se499.kayaanbackend.Manual_Generate.dto.UnifiedContentResponse;
import se499.kayaanbackend.Manual_Generate.service.ContentTransformationService;
import se499.kayaanbackend.Manual_Generate.service.ManualGeneratedContentService;
import se499.kayaanbackend.security.user.User;

/**
 * Unified API controller for both AI and Manual content
 * Provides single endpoint for Frontend to access all content types
 */
@RestController
@RequestMapping("/api/content")
@RequiredArgsConstructor
@Slf4j
public class UnifiedContentController {
    
    private final AIGenerationService aiGenerationService;
    private final ContentTransformationService transformationService;
    private final ManualGeneratedContentService manualGeneratedContentService;
    
    /**
     * Get unified content for authenticated user
     * Combines both AI and Manual content in a single response
     * 
     * @param user Authenticated user
     * @param page Page number (0-based)
     * @param size Page size
     * @param contentType Filter by content type (quiz, flashcard, note, all)
     * @param source Filter by source (ai, manual, all)
     * @param sortBy Sort field (createdAt, title, contentType)
     * @param sortDir Sort direction (asc, desc)
     * @return Unified content response with pagination
     */
    @GetMapping("/user")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UnifiedContentResponse> getUserContent(
            @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "all") String contentType,
            @RequestParam(defaultValue = "all") String source,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir
    ) {
        try {
            log.info("Getting unified content for user: {} (page: {}, size: {}, type: {}, source: {})", 
                    user.getUsername(), page, size, contentType, source);
            
            List<UnifiedContentDTO> allContent = new ArrayList<>();
            int totalAiContent = 0;
            int totalManualContent = 0;
            int totalQuizzes = 0;
            int totalFlashcards = 0;
            int totalNotes = 0;
            
            // Get AI Content (if requested)
            if ("all".equals(source) || "ai".equals(source)) {
                try {
                    Pageable aiPageable = PageRequest.of(0, 1000); // Get all for filtering
                    Page<AIGeneratedContentDTO> aiContent = aiGenerationService.getUserSavedContent(user.getId().longValue(), aiPageable);
                    
                    List<UnifiedContentDTO> aiUnified = aiContent.getContent().stream()
                        .filter(content -> "all".equals(contentType) || contentType.equals(content.getContentType()))
                        .map(this::transformAIContentToUnified)
                        .collect(Collectors.toList());
                        
                    allContent.addAll(aiUnified);
                    totalAiContent = aiUnified.size();
                    
                    // Count by type
                    totalQuizzes += aiUnified.stream().mapToInt(c -> "quiz".equals(c.getContentType()) ? 1 : 0).sum();
                    totalFlashcards += aiUnified.stream().mapToInt(c -> "flashcard".equals(c.getContentType()) ? 1 : 0).sum();
                    totalNotes += aiUnified.stream().mapToInt(c -> "note".equals(c.getContentType()) ? 1 : 0).sum();
                    
                } catch (Exception e) {
                    log.warn("Failed to get AI content for user {}: {}", user.getUsername(), e.getMessage());
                }
            }
            
            // Get Manual Content (if requested)
            if ("all".equals(source) || "manual".equals(source)) {
                try {
                    // Get Quiz content from NEW ManualGeneratedContentService
                    if ("all".equals(contentType) || "quiz".equals(contentType)) {
                        List<ManualGeneratedContentDTO> quizzes = manualGeneratedContentService.getContentByTypeForUser(user.getUsername(), se499.kayaanbackend.AI_Generate.entity.ContentType.QUIZ);
                        List<UnifiedContentDTO> quizUnified = quizzes.stream()
                            .map(this::transformManualContentToUnified)
                            .collect(Collectors.toList());
                        allContent.addAll(quizUnified);
                        totalQuizzes += quizUnified.size();
                        totalManualContent += quizUnified.size();
                    }
                    
                    // Get Flashcard content from NEW ManualGeneratedContentService
                    if ("all".equals(contentType) || "flashcard".equals(contentType)) {
                        List<ManualGeneratedContentDTO> flashcards = manualGeneratedContentService.getContentByTypeForUser(user.getUsername(), se499.kayaanbackend.AI_Generate.entity.ContentType.FLASHCARD);
                        List<UnifiedContentDTO> flashcardUnified = flashcards.stream()
                            .map(this::transformManualContentToUnified)
                            .collect(Collectors.toList());
                        allContent.addAll(flashcardUnified);
                        totalFlashcards += flashcardUnified.size();
                        totalManualContent += flashcardUnified.size();
                    }
                    
                    // Get Note content from NEW ManualGeneratedContentService
                    if ("all".equals(contentType) || "note".equals(contentType)) {
                        List<ManualGeneratedContentDTO> notes = manualGeneratedContentService.getContentByTypeForUser(user.getUsername(), se499.kayaanbackend.AI_Generate.entity.ContentType.NOTE);
                        List<UnifiedContentDTO> noteUnified = notes.stream()
                            .map(this::transformManualContentToUnified)
                            .collect(Collectors.toList());
                        allContent.addAll(noteUnified);
                        totalNotes += noteUnified.size();
                        totalManualContent += noteUnified.size();
                    }
                    
                } catch (Exception e) {
                    log.warn("Failed to get manual content for user {}: {}", user.getUsername(), e.getMessage());
                }
            }
            
            // Sort content
            Comparator<UnifiedContentDTO> comparator = getComparator(sortBy, sortDir);
            allContent.sort(comparator);
            
            // Apply pagination
            int start = page * size;
            int end = Math.min(start + size, allContent.size());
            List<UnifiedContentDTO> paginatedContent = start < allContent.size() ? 
                allContent.subList(start, end) : List.of();
            
            // Build summary
            UnifiedContentResponse.ContentSummary summary = UnifiedContentResponse.ContentSummary.builder()
                .totalAiContent(totalAiContent)
                .totalManualContent(totalManualContent)
                .totalQuizzes(totalQuizzes)
                .totalFlashcards(totalFlashcards)
                .totalNotes(totalNotes)
                .build();
            
            UnifiedContentResponse response = UnifiedContentResponse.builder()
                .content(paginatedContent)
                .totalElements(allContent.size())
                .currentPage(page)
                .totalPages((int) Math.ceil((double) allContent.size() / size))
                .size(size)
                .summary(summary)
                .success(true)
                .build();
                
            log.info("Successfully retrieved {} items for user {} (AI: {}, Manual: {})", 
                    allContent.size(), user.getUsername(), totalAiContent, totalManualContent);
                
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            log.error("Error getting unified content for user: {}", user.getUsername(), e);
            
            UnifiedContentResponse errorResponse = UnifiedContentResponse.builder()
                .error("Failed to get content: " + e.getMessage())
                .success(false)
                .content(List.of())
                .totalElements(0)
                .currentPage(page)
                .totalPages(0)
                .size(size)
                .build();
                
            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
    
    /**
     * Get content statistics for user dashboard
     */
    @GetMapping("/user/stats")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UnifiedContentResponse.ContentSummary> getUserContentStats(
            @AuthenticationPrincipal User user
    ) {
        try {
            log.info("Getting content statistics for user: {}", user.getUsername());
            
            int totalAiContent = 0;
            int totalManualContent = 0;
            int totalQuizzes = 0;
            int totalFlashcards = 0;
            int totalNotes = 0;
            
            // Count AI content
            try {
                Pageable countPageable = PageRequest.of(0, Integer.MAX_VALUE);
                Page<AIGeneratedContentDTO> aiContent = aiGenerationService.getUserSavedContent(user.getId().longValue(), countPageable);
                totalAiContent = (int) aiContent.getTotalElements();
                
                for (AIGeneratedContentDTO content : aiContent.getContent()) {
                    switch (content.getContentType()) {
                        case "quiz" -> totalQuizzes++;
                        case "flashcard" -> totalFlashcards++;
                        case "note" -> totalNotes++;
                    }
                }
            } catch (Exception e) {
                log.warn("Failed to count AI content: {}", e.getMessage());
            }
            
            // Count Manual content from NEW ManualGeneratedContentService
            try {
                List<ManualGeneratedContentDTO> quizzes = manualGeneratedContentService.getContentByTypeForUser(user.getUsername(), se499.kayaanbackend.AI_Generate.entity.ContentType.QUIZ);
                List<ManualGeneratedContentDTO> flashcards = manualGeneratedContentService.getContentByTypeForUser(user.getUsername(), se499.kayaanbackend.AI_Generate.entity.ContentType.FLASHCARD);
                List<ManualGeneratedContentDTO> notes = manualGeneratedContentService.getContentByTypeForUser(user.getUsername(), se499.kayaanbackend.AI_Generate.entity.ContentType.NOTE);
                
                totalQuizzes += quizzes.size();
                totalFlashcards += flashcards.size();
                totalNotes += notes.size();
                totalManualContent = quizzes.size() + flashcards.size() + notes.size();
            } catch (Exception e) {
                log.warn("Failed to count manual content: {}", e.getMessage());
            }
            
            UnifiedContentResponse.ContentSummary summary = UnifiedContentResponse.ContentSummary.builder()
                .totalAiContent(totalAiContent)
                .totalManualContent(totalManualContent)
                .totalQuizzes(totalQuizzes)
                .totalFlashcards(totalFlashcards)
                .totalNotes(totalNotes)
                .build();
                
            return ResponseEntity.ok(summary);
            
        } catch (Exception e) {
            log.error("Error getting content statistics for user: {}", user.getUsername(), e);
            return ResponseEntity.badRequest().build();
        }
    }
    
    /**
     * Transform Manual Generated Content to unified format
     */
    private UnifiedContentDTO transformManualContentToUnified(ManualGeneratedContentDTO manualContent) {
        return UnifiedContentDTO.builder()
            .id("manual-" + manualContent.getContentType() + "-" + manualContent.getId())
            .title(manualContent.getContentTitle())
            .contentType(manualContent.getContentType())
            .source("manual")
            .content(manualContent.getContentData())
            .createdAt(manualContent.getCreatedAt())
            .updatedAt(manualContent.getUpdatedAt())
            .difficulty(manualContent.getDifficulty())
            .subject(manualContent.getSubject())
            .tags(manualContent.getTags() != null ? manualContent.getTags() : List.of())
            .createdByUsername(manualContent.getUsername())
            .build();
    }
    
    /**
     * Transform AI content to unified format
     */
    private UnifiedContentDTO transformAIContentToUnified(AIGeneratedContentDTO aiContent) {
        return UnifiedContentDTO.builder()
            .id("ai-" + aiContent.getId())
            .title(aiContent.getContentTitle())
            .contentType(aiContent.getContentType())
            .source("ai")
            .content(aiContent.getContentData())
            .createdAt(aiContent.getCreatedAt())
            .updatedAt(aiContent.getUpdatedAt())
            .difficulty(null) // AI content doesn't have difficulty
            .subject(null)    // AI content doesn't have subject
            .tags(List.of())  // AI content doesn't have tags
            .createdByUsername(null) // AI content doesn't have username
            .aiRequestId(aiContent.getGenerationRequestId())
            .supabaseFilePath(aiContent.getSupabaseFilePath())
            .fileSize(aiContent.getFileSize())
            .isSaved(aiContent.getIsSaved())
            .build();
    }
    
    /**
     * Get comparator for sorting
     */
    private Comparator<UnifiedContentDTO> getComparator(String sortBy, String sortDir) {
        Comparator<UnifiedContentDTO> comparator = switch (sortBy.toLowerCase()) {
            case "title" -> Comparator.comparing(UnifiedContentDTO::getTitle, String.CASE_INSENSITIVE_ORDER);
            case "contenttype" -> Comparator.comparing(UnifiedContentDTO::getContentType);
            case "source" -> Comparator.comparing(UnifiedContentDTO::getSource);
            case "updatedat" -> Comparator.comparing(c -> c.getUpdatedAt() != null ? c.getUpdatedAt() : c.getCreatedAt());
            default -> Comparator.comparing(UnifiedContentDTO::getCreatedAt); // Default to createdAt
        };
        
        return "asc".equalsIgnoreCase(sortDir) ? comparator : comparator.reversed();
    }
}

package se499.kayaanbackend.Manual_Generate.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.Manual_Generate.Flashcard.dto.FlashcardResponseDTO;
import se499.kayaanbackend.Manual_Generate.Note.dto.NoteResponseDTO;
import se499.kayaanbackend.Manual_Generate.Quiz.dto.QuizResponseDTO;
import se499.kayaanbackend.Manual_Generate.dto.UnifiedContentDTO;

/**
 * Service for transforming Manual content to AI-compatible format
 * This enables unified display of both Manual and AI content in Frontend
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class ContentTransformationService {
    
    private final ObjectMapper objectMapper;
    
    /**
     * Transform Manual Quiz to AI-compatible format
     */
    public UnifiedContentDTO transformQuizToUnified(QuizResponseDTO quiz) {
        try {
            log.debug("Transforming quiz '{}' to unified format", quiz.getTitle());
            
            // Transform questions to AI format
            List<Map<String, Object>> transformedQuestions = quiz.getQuestions().stream()
                .map(this::transformQuestionToAIFormat)
                .collect(Collectors.toList());
            
            // Create AI-compatible structure
            Map<String, Object> metadata = Map.of(
                "title", quiz.getTitle(),
                "contentType", "quiz",
                "source", "manual",
                "createdAt", quiz.getCreatedAt() != null ? quiz.getCreatedAt().toString() : "",
                "updatedAt", quiz.getUpdatedAt() != null ? quiz.getUpdatedAt().toString() : ""
            );
            
            Map<String, Object> content = Map.of(
                "questions", transformedQuestions
            );
            
            Map<String, Object> unifiedFormat = Map.of(
                "metadata", metadata,
                "content", content
            );
            
            return UnifiedContentDTO.builder()
                .id("manual-quiz-" + quiz.getId())
                .title(quiz.getTitle())
                .contentType("quiz")
                .source("manual")
                .content(objectMapper.writeValueAsString(unifiedFormat))
                .createdAt(quiz.getCreatedAt())
                .updatedAt(quiz.getUpdatedAt())
                .difficulty(quiz.getDifficulty())
                .subject(quiz.getSubject())
                .tags(quiz.getTags() != null ? List.of(quiz.getTags().split(",")) : List.of())
                .createdByUsername(quiz.getCreatedByUsername())
                .build();
                
        } catch (JsonProcessingException e) {
            log.error("Error transforming quiz to unified format: {}", quiz.getTitle(), e);
            throw new RuntimeException("Failed to transform quiz: " + e.getMessage());
        }
    }
    
    /**
     * Transform Manual Flashcard to AI-compatible format
     */
    public UnifiedContentDTO transformFlashcardToUnified(FlashcardResponseDTO flashcard) {
        try {
            log.debug("Transforming flashcard to unified format");
            
            // Create AI-compatible flashcard structure
            List<Map<String, Object>> flashcards = List.of(
                Map.of(
                    "question", flashcard.getFrontText(),
                    "answer", flashcard.getBackText()
                )
            );
            
            Map<String, Object> metadata = Map.of(
                "title", extractTitleFromFlashcard(flashcard),
                "contentType", "flashcard",
                "source", "manual",
                "createdAt", flashcard.getCreatedAt() != null ? flashcard.getCreatedAt().toString() : "",
                "updatedAt", flashcard.getUpdatedAt() != null ? flashcard.getUpdatedAt().toString() : ""
            );
            
            Map<String, Object> content = Map.of(
                "flashcards", flashcards
            );
            
            Map<String, Object> unifiedFormat = Map.of(
                "metadata", metadata,
                "content", content
            );
            
            return UnifiedContentDTO.builder()
                .id("manual-flashcard-" + flashcard.getId())
                .title(extractTitleFromFlashcard(flashcard))
                .contentType("flashcard")
                .source("manual")
                .content(objectMapper.writeValueAsString(unifiedFormat))
                .createdAt(flashcard.getCreatedAt())
                .updatedAt(flashcard.getUpdatedAt())
                .difficulty(flashcard.getDifficulty())
                .subject(flashcard.getSubject())
                .tags(flashcard.getTags() != null ? flashcard.getTags() : List.of())
                .createdByUsername(flashcard.getCreatedByUsername())
                .build();
                
        } catch (JsonProcessingException e) {
            log.error("Error transforming flashcard to unified format", e);
            throw new RuntimeException("Failed to transform flashcard: " + e.getMessage());
        }
    }
    
    /**
     * Transform Manual Note to AI-compatible format
     */
    public UnifiedContentDTO transformNoteToUnified(NoteResponseDTO note) {
        try {
            log.debug("Transforming note '{}' to unified format", note.getTitle());
            
            // Create AI-compatible note structure
            List<Map<String, Object>> contentSections = List.of(
                Map.of(
                    "feature", note.getTitle(),
                    "description", note.getContent()
                )
            );
            
            Map<String, Object> metadata = Map.of(
                "title", note.getTitle(),
                "contentType", "note",
                "source", "manual",
                "createdAt", note.getCreatedAt() != null ? note.getCreatedAt().toString() : "",
                "updatedAt", note.getUpdatedAt() != null ? note.getUpdatedAt().toString() : ""
            );
            
            Map<String, Object> content = Map.of(
                "content", contentSections
            );
            
            Map<String, Object> unifiedFormat = Map.of(
                "metadata", metadata,
                "content", content
            );
            
            return UnifiedContentDTO.builder()
                .id("manual-note-" + note.getId())
                .title(note.getTitle())
                .contentType("note")
                .source("manual")
                .content(objectMapper.writeValueAsString(unifiedFormat))
                .createdAt(note.getCreatedAt())
                .updatedAt(note.getUpdatedAt())
                .difficulty(note.getDifficulty())
                .subject(note.getSubject())
                .tags(note.getTags() != null ? note.getTags() : List.of())
                .createdByUsername(note.getCreatedByUsername())
                .build();
                
        } catch (JsonProcessingException e) {
            log.error("Error transforming note to unified format: {}", note.getTitle(), e);
            throw new RuntimeException("Failed to transform note: " + e.getMessage());
        }
    }
    
    /**
     * Transform individual quiz question to AI format
     */
    private Map<String, Object> transformQuestionToAIFormat(QuizResponseDTO.QuestionResponse question) {
        Map<String, Object> transformed = new HashMap<>();
        
        transformed.put("id", question.getId());
        transformed.put("question", question.getQuestionText());
        transformed.put("type", question.getType().toString().toLowerCase().replace("_", "-"));
        
        // Handle different question types
        switch (question.getType()) {
            case MULTIPLE_CHOICE:
                if (question.getChoices() != null && !question.getChoices().isEmpty()) {
                    List<Map<String, Object>> options = IntStream.range(0, question.getChoices().size())
                        .mapToObj(i -> {
                            Map<String, Object> option = new HashMap<>();
                            option.put("id", String.valueOf((char) ('A' + i))); // A, B, C, D
                            option.put("text", question.getChoices().get(i));
                            option.put("correct", question.getChoices().get(i).equals(question.getCorrectAnswer()));
                            return option;
                        })
                        .collect(Collectors.toList());
                    transformed.put("options", options);
                }
                break;
                
            case TRUE_FALSE: {
                List<Map<String, Object>> tfOptions = new ArrayList<>();
                
                Map<String, Object> trueOption = new HashMap<>();
                trueOption.put("id", "true");
                trueOption.put("text", "True");
                trueOption.put("correct", "true".equalsIgnoreCase(question.getCorrectAnswer()));
                tfOptions.add(trueOption);
                
                Map<String, Object> falseOption = new HashMap<>();
                falseOption.put("id", "false");
                falseOption.put("text", "False");
                falseOption.put("correct", "false".equalsIgnoreCase(question.getCorrectAnswer()));
                tfOptions.add(falseOption);
                
                transformed.put("options", tfOptions);
                break;
            }
                
            case OPEN_ENDED:
                // For open-ended questions, explicitly set options to null
                // This helps frontend distinguish from multiple choice questions
                transformed.put("options", null);
                break;
        }
        
        transformed.put("correctAnswer", question.getCorrectAnswer());
        transformed.put("explanation", null); // Manual questions don't have explanations yet
        
        return transformed;
    }
    
    /**
     * Extract a meaningful title from flashcard (using front text or subject)
     */
    private String extractTitleFromFlashcard(FlashcardResponseDTO flashcard) {
        if (flashcard.getSubject() != null && !flashcard.getSubject().trim().isEmpty()) {
            return flashcard.getSubject() + " Flashcard";
        }
        
        String frontText = flashcard.getFrontText();
        if (frontText.length() > 50) {
            return frontText.substring(0, 47) + "...";
        }
        
        return frontText;
    }
}

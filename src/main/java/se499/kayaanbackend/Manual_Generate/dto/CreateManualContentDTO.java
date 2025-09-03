package se499.kayaanbackend.Manual_Generate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * DTO for creating manual content in JSON format
 * This replaces the individual QuizRequestDTO, NoteRequestDTO, FlashcardRequestDTO
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateManualContentDTO {
    
    @NotBlank(message = "Content title is required")
    @Size(min = 1, max = 255, message = "Content title must be between 1 and 255 characters")
    private String contentTitle;
    
    @NotBlank(message = "Content type is required")
    private String contentType; // "flashcard", "quiz", "note"
    
    @NotBlank(message = "Content data is required")
    private String contentData; // JSON string of the actual content
    
    private String subject;
    
    private String difficulty;
    
    private List<String> tags;
    
    // Additional metadata
    @Builder.Default
    private Integer contentVersion = 1;
    
    @Builder.Default
    private Boolean isSaved = true;
}

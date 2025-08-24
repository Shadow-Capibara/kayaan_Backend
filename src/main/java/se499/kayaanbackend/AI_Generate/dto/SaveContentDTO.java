package se499.kayaanbackend.AI_Generate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaveContentDTO {
    
    @NotNull(message = "Generation request ID is required")
    private Long generationRequestId;
    
    @NotBlank(message = "Content title is required")
    @Size(min = 1, max = 255, message = "Content title must be between 1 and 255 characters")
    private String contentTitle;
    
    @NotBlank(message = "Content type is required")
    @Size(max = 50, message = "Content type must not exceed 50 characters")
    private String contentType; // e.g., "flashcard", "quiz", "note", "summary"
    
    @NotBlank(message = "Content data is required")
    private String contentData; // JSON string of AI-generated content
    
    @Builder.Default
    private Boolean saveToSupabase = true; // Whether to also save to Supabase Storage
    
    @Builder.Default
    private String customFileName = null; // Optional custom filename for Supabase
}

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
public class PreviewRequestDTO {
    
    @NotNull(message = "Generation request ID is required")
    private Long generationRequestId;
    
    @NotBlank(message = "Content title is required")
    @Size(min = 1, max = 255, message = "Content title must be between 1 and 255 characters")
    private String contentTitle;
    
    @NotBlank(message = "Content type is required")
    @Size(max = 50, message = "Content type must not exceed 50 characters")
    private String contentType; // "flashcard", "quiz", "note"
    
    @NotBlank(message = "Content data is required")
    private String contentData; // JSON string of AI-generated content
    
    @Builder.Default
    private Boolean includeMetadata = true; // Whether to include generation metadata in preview
    
    @Builder.Default
    private String previewFormat = "formatted"; // "formatted", "raw", "minimal"
}

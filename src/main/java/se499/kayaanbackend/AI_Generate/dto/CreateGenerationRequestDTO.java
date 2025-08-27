package se499.kayaanbackend.AI_Generate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateGenerationRequestDTO {
    
    @NotBlank(message = "Prompt text is required")
    @Size(min = 10, max = 2000, message = "Prompt text must be between 10 and 2000 characters")
    private String promptText;
    
    @NotBlank(message = "Output format is required")
    @Size(max = 50, message = "Output format must not exceed 50 characters")
    private String outputFormat; // e.g., "flashcard", "quiz", "note", "summary"
    
    @Size(max = 500, message = "Additional context must not exceed 500 characters")
    private String additionalContext;
    
    // File upload support
    private MultipartFile uploadedFile; // PDF, DOCX, TXT, Image files
    
    private String fileType; // "pdf", "docx", "txt", "image"
    
    private String fileName; // Original filename
    
    @Builder.Default
    private Integer maxRetries = 3;
    
    @Builder.Default
    private Boolean useTemplate = false;
    
    private Long templateId; // Optional: if using existing template
    
    // File processing options
    @Builder.Default
    private Boolean extractTextFromFile = true; // Whether to extract text from uploaded file
    
    @Builder.Default
    private Boolean useFileContentAsContext = true; // Whether to use file content as additional context
}

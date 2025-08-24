package se499.kayaanbackend.AI_Generate.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AIPromptTemplateDTO {
    
    private Long id;
    
    @NotBlank(message = "Template name is required")
    @Size(min = 1, max = 255, message = "Template name must be between 1 and 255 characters")
    private String templateName;
    
    @Size(max = 1000, message = "Template description must not exceed 1000 characters")
    private String templateDescription;
    
    @NotBlank(message = "Prompt text is required")
    @Size(min = 10, max = 2000, message = "Prompt text must be between 10 and 2000 characters")
    private String promptText;
    
    @NotBlank(message = "Output format is required")
    @Size(max = 50, message = "Output format must not exceed 50 characters")
    private String outputFormat;
    
    @Builder.Default
    private Boolean isPublic = false;
    
    @Builder.Default
    private Boolean isActive = true;
    
    private Integer usageCount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

package se499.kayaanbackend.AI_Generate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se499.kayaanbackend.AI_Generate.entity.AIGenerationRequest.GenerationStatus;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GenerationStatusDTO {
    
    private Long id;
    private Long userId;
    private String promptText;
    private String outputFormat;
    private GenerationStatus status;
    private Integer progress; // 0-100
    private String errorMessage;
    private Integer retryCount;
    private Integer maxRetries;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    
    // Additional fields for enhanced status
    private String estimatedTimeRemaining;
    private String currentStep;
    private Boolean canCancel;
    private Boolean canRetry;
}

package se499.kayaanbackend.AI_Generate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO for file upload response
 * Contains information about uploaded file and processing status
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponseDTO {
    
    private String fileName;
    private String fileType;
    private long fileSize;
    private String processingStatus; // "success", "processing", "failed"
    private String extractedText; // Text content extracted from file
    private String errorMessage; // Error message if processing failed
    private int textLength; // Length of extracted text
    private boolean isValidForAI; // Whether file content is suitable for AI processing
    
    // File metadata
    private String contentType;
    private String originalExtension;
    private String processedAt;
    
    // AI processing hints
    private String suggestedPrompt; // Suggested prompt based on file content
    private String contentSummary; // Brief summary of file content
    private String[] keywords; // Key terms extracted from file
}

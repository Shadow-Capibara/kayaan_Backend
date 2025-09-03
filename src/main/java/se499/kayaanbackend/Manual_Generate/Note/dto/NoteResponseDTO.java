package se499.kayaanbackend.Manual_Generate.Note.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class NoteResponseDTO {
    
    private Long id;
    private String createdByUsername;
    private String title;
    private String content;
    private String subject;
    private String difficulty;
    private List<String> tags; // Changed to List<String> for Frontend compatibility
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

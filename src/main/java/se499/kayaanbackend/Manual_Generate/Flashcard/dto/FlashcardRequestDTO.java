package se499.kayaanbackend.Manual_Generate.Flashcard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardRequestDTO {
    
    private String frontText;
    private String backText;
    private String subject;
    private String difficulty;
    private List<String> tags; // Changed from String to List<String> to match Frontend
}

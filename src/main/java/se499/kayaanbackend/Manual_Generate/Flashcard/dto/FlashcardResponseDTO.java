package se499.kayaanbackend.Manual_Generate.Flashcard.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FlashcardResponseDTO {
    
    private Long id;
    private String createdByUsername;
    private String frontText;
    private String backText;
    private String subject;
    private String difficulty;
    private String tags;

}

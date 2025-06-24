package se499.kayaanbackend.Manual_Generate.Flashcard.dto;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashcardResponseDTO {
    private Long id;
    private String createdByUsername;
    private String frontText;
    private String backText;
    private String subject;
    private String difficulty;
    private java.util.List<String> tags;
}

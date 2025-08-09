package se499.kayaanbackend.Manual_Generate.Flashcard.dto;


import lombok.*;
import jakarta.validation.constraints.NotBlank;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashcardRequestDTO {
    @NotBlank
    private String frontText;
    @NotBlank
    private String backText;
    private String subject;
    private String difficulty;
    private String category;
    private String frontImageUrl;
    private String backImageUrl;
    private List<String> tags;
    private List<Long> groupIds;
}

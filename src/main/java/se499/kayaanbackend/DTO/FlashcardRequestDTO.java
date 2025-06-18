package se499.kayaanbackend.DTO;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashcardRequestDTO {
    private String frontText;
    private String backText;
    private String subject;
    private String difficulty;
    private List<String> tags;
}

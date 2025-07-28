package se499.kayaanbackend.Manual_Generate.Quiz.dto;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRequestDTO {
    private String title;
    private String subject;
    private String difficulty;
    private List<String> tags;
    private List<QuizQuestionRequestDTO> questions;
}

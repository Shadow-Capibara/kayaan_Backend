package se499.kayaanbackend.Manual_Generate.Quiz.dto;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseDTO {
    
    private Long id;
    private String title;
    private String createdByUsername;

    private List<QuestionResponse> questions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResponse {
        private Long id;
        private String question;
        private List<String> choices;
        private String correctAnswer;
    }
}

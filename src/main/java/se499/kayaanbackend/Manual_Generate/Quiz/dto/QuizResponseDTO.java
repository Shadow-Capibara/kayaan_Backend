package se499.kayaanbackend.Manual_Generate.Quiz.dto;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import se499.kayaanbackend.Manual_Generate.entity.ManualQuizQuestion;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class QuizResponseDTO {
    
    private Long id;
    private String title;
    private String createdByUsername;
    private String subject;
    private String difficulty;
    private String tags;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<QuestionResponse> questions;
    
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class QuestionResponse {
        private Long id;
        private String questionText;
        private ManualQuizQuestion.QuestionType type;
        private List<String> choices;
        private String correctAnswer;
        private Integer questionOrder;
        private String subject;
        private String difficulty;
        private String tags;
    }
}

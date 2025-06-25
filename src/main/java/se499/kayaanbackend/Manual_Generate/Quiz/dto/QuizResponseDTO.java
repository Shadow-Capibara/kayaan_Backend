package se499.kayaanbackend.Manual_Generate.Quiz.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class QuizResponseDTO {
    private Long id;
    private String title;
    private String createdByUsername;
    private String category;
    private List<Long> groupIds;

    // Return the questions back as a list of QuestionResponse (or reuse QuestionRequestDto here)
    private List<QuestionResponse> questions;

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class QuestionResponse {
        private Long id;
        private String questionText;
        private QuizQuestionRequestDTO.QuestionType type;
        private List<String> choices;
        private String correctAnswer;
        private String subject;
        private String difficulty;
        private List<String> tags;
    }
}

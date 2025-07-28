package se499.kayaanbackend.Manual_Generate.Quiz.dto;


import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionRequestDTO {
    private String questionText;
    private QuestionType type;

    // For MULTIPLE_CHOICE: a non-empty list of choices (e.g. ["Option A","Option B","Option C"])
    // Otherwise can be null or empty
    private List<String> choices;

    // For MCQ or TRUE_FALSE: this is the correct answer ("A", "true", etc.)
    // For OPEN_ENDED, can store sample answer or leave blank
    private String correctAnswer;

    private String subject;
    private String difficulty;

    public enum QuestionType {
        MULTIPLE_CHOICE,
        TRUE_FALSE,
        OPEN_ENDED
    }
}

package se499.kayaanbackend.content.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuizDto {
    private Long quizId;
    private Long contentId;
    private String title;
    private List<QuizQuestionDto> questions;
}

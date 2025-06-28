package se499.kayaanbackend.content.dto;

import lombok.Data;
import java.util.List;

@Data
public class QuizQuestionDto {
    private Long questionId;
    private String questionText;
    private List<QuizChoiceDto> choices;
}

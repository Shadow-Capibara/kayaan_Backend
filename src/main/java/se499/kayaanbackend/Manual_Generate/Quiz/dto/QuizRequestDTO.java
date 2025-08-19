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
public class QuizRequestDTO {
    
    private String title;

    private List<QuizQuestionRequestDTO> questions;
}

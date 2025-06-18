package se499.kayaanbackend.DTO;


import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizRequestDTO {

    private String title;

    private List<QuizQuestionRequestDTO> questions;
}

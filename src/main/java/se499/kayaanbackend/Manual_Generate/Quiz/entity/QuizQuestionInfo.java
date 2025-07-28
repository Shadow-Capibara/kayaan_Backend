package se499.kayaanbackend.Manual_Generate.Quiz.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_question_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer questionID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quizInfoID", nullable = false)
    private QuizInfo quizInformation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType questionType;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String questionText;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String correctAnswer;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;

    public enum QuestionType { MultipleChoice, TrueFalse, OpenEnded }
}

package se499.kayaanbackend.Manual_Generate.Quiz.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.Set;

@Entity
@Table(name = "QUIZ_QUESTION")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "question_id")
    private Integer questionId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "question_type")
    private QuestionType questionType;

    @Column(name = "choices", columnDefinition = "TEXT")
    private String choices; // JSON string for multiple choices

    @Column(name = "correct_answer", nullable = false, columnDefinition = "TEXT")
    private String correctAnswer;

    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizQuestionChoice> questionChoices;

    public enum QuestionType {
        MULTIPLE_CHOICE, TRUE_FALSE, OPEN_END
    }
}

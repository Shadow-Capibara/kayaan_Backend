package se499.kayaanbackend.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "quiz_question_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestion {

    public enum QuestionType {
        MULTIPLE_CHOICE,
        TRUE_FALSE,
        OPEN_ENDED
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "questionID")
    private Long id;

    // Link back to parent quiz
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quizInfoID", nullable = false)
    private Quiz quiz;

    @Column(name = "question_text", nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(name = "questionType", nullable = false)
    private QuestionType type;

    // For MCQ only: store the choices as a list of strings
    // For True/False or Open-Ended, you can leave this empty
    @OneToMany(mappedBy = "question", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizQuestionChoice> choices;

    // For MCQ/TrueFalse: the correct answer (e.g. "A" or "true")
    // For Open-Ended: you could store sample answers or leave blank (depending on your design)
    @Column(name = "correct_answer", columnDefinition = "TEXT")
    private String correctAnswer;

    // Optional metadata: subject, difficulty (you can expand these into enums if you want)
    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;
}

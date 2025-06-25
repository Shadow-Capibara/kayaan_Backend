package se499.kayaanbackend.Manual_Generate.Quiz.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "questions")
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
    private Long id;

    // Link back to parent quiz
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "quiz_id", nullable = false)
    private Quiz quiz;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String questionText;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuestionType type;

    // For MCQ only: store the choices as a list of strings
    // For True/False or Open-Ended, you can leave this empty
    @ElementCollection
    @CollectionTable(name = "question_choices", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "choice")
    private List<String> choices;

    // For MCQ/TrueFalse: the correct answer (e.g. "A" or "true")
    // For Open-Ended: you could store sample answers or leave blank (depending on your design)
    @Column(nullable = true, columnDefinition = "TEXT")
    private String correctAnswer;

    // Optional metadata: subject, difficulty (you can expand these into enums if you want)
    @Column(nullable = true)
    private String subject;

    @Column(nullable = true)
    private String difficulty;

    // Tags as a simple list of strings (e.g. ["algebra","easy"])
    @ElementCollection
    @CollectionTable(name = "question_tags", joinColumns = @JoinColumn(name = "question_id"))
    @Column(name = "tag")
    private List<String> tags;
}

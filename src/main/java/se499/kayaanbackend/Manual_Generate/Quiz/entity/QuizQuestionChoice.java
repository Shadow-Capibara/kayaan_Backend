package se499.kayaanbackend.Manual_Generate.Quiz.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "QUESTION_CHOICE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "choice_id")
    private Integer choiceID;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionID", nullable = false)
    private QuizQuestion question;

    @Column(name = "choiceDetail", columnDefinition = "TEXT", nullable = false)
    private String choiceDetail;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
}

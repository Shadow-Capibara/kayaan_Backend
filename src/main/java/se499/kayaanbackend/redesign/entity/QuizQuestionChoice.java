package se499.kayaanbackend.redesign.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_question_choice")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizQuestionChoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer choiceID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "questionID", nullable = false)
    private QuizQuestionInformation question;

    @Column(name = "choiceDetail", columnDefinition = "TEXT", nullable = false)
    private String choiceDetail;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
}

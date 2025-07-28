package se499.kayaanbackend.Manual_Generate.Quiz.entity;

import jakarta.persistence.*;
import lombok.*;
import se499.kayaanbackend.Manual_Generate.contentInfo.entity.ContentInfo;

import java.time.LocalDateTime;

@Entity
@Table(name = "quiz_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer quizInfoID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contentInfoID", nullable = false)
    private ContentInfo contentInformation;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizType quizType;

    @Column(name = "quiz_detail", columnDefinition = "TEXT", nullable = false)
    private String quizDetail;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;

    public enum QuizType { MultipleChoice, TrueFalse, OpenEnded }
}

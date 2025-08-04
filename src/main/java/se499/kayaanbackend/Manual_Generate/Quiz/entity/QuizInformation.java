package se499.kayaanbackend.Manual_Generate.Quiz.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se499.kayaanbackend.common.entity.ContentInformation;

@Entity
@Table(name = "quiz_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class QuizInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer quizInfoID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_infoid", nullable = false)
    private ContentInformation contentInformation;

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

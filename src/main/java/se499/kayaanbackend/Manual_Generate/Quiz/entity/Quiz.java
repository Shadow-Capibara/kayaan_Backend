package se499.kayaanbackend.Manual_Generate.Quiz.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se499.kayaanbackend.Manual_Generate.contentInfo.entity.ContentInfo;
import se499.kayaanbackend.Study_Group.entity.Group;
import se499.kayaanbackend.common.entity.ContentInformation;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "QUIZ")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_infoid", nullable = false)
    private ContentInfo contentInfo;

    @Column(name = "quiz_detail", columnDefinition = "TEXT", nullable = false)
    private String quizDetail;

    @Enumerated(EnumType.STRING)
    @OneToMany(mappedBy = "QUIZ", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizQuestion> questions;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private QuizType quizType;

    public enum QuizType { MultipleChoice, TrueFalse, OpenEnded }

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}

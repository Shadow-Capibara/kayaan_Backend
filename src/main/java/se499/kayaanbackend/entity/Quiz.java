package se499.kayaanbackend.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.List;

import se499.kayaanbackend.entity.QuizImage;

@Entity
@Table(name = "quiz_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quiz {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "quizInfoID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contentInfoID", nullable = false)
    private ContentInformation contentInformation;

    @Enumerated(EnumType.STRING)
    @Column(name = "quizType", nullable = false)
    private QuizType quizType;

    @Column(name = "quiz_detail", columnDefinition = "TEXT", nullable = false)
    private String quizDetail;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizQuestion> questions;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<QuizImage> images;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;

    public enum QuizType {
        MULTIPLE_CHOICE, TRUE_FALSE, OPEN_ENDED
    }
}

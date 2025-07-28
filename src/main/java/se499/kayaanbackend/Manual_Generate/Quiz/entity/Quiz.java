package se499.kayaanbackend.Manual_Generate.Quiz.entity;


import jakarta.persistence.*;
import lombok.*;

import se499.kayaanbackend.Manual_Generate.Group.entity.Group;
import se499.kayaanbackend.Manual_Generate.contentInfo.entity.ContentInfo;

import java.util.List;
import java.util.Set;

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
    @Column(name = "quiz_id")
    private Integer quizId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private ContentInfo content;

    @OneToMany(mappedBy = "quiz", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<QuizQuestion> questions;
}

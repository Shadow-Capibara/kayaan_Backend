package se499.kayaanbackend.redesign.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "flashcard_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashcardInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer flashcardID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contentInfoID", nullable = false)
    private ContentInformation contentInformation;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String flashcardDetail;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String flashcardAnswer;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
}

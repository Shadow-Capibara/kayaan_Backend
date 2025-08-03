package se499.kayaanbackend.Manual_Generate.Flashcard.entity;

import jakarta.persistence.*;
import lombok.*;

import se499.kayaanbackend.Manual_Generate.Group.entity.Group;
import se499.kayaanbackend.Manual_Generate.contentInfo.entity.ContentInfo;

import java.time.LocalDateTime;

@Entity
@Table(name = "FLASHCARD")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flashcard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flashcard_id")
    private Long flashcardId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private ContentInfo content;

    @Column(name = "front_text", nullable = false, columnDefinition = "TEXT")
    private String frontText;

    @Column(name = "back_text", nullable = false, columnDefinition = "TEXT")
    private String backText;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;

}

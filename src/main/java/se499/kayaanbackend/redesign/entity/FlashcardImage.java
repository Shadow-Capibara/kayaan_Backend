package se499.kayaanbackend.redesign.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "flashcard_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FlashcardImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "flashcardID", nullable = false)
    private FlashcardInfo flashcardInfo;

    @Column(nullable = false)
    private String imageURL;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
}

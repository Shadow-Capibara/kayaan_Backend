package se499.kayaanbackend.entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

import se499.kayaanbackend.entity.FlashcardImage;

@Entity
@Table(name = "flashcard_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flashcard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "flashcardID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contentInfoID", nullable = false)
    private ContentInformation contentInformation;

    @Column(name = "flashcardDetail", nullable = false, columnDefinition = "TEXT")
    private String frontText;

    @Column(name = "flashcardAnswer", nullable = false, columnDefinition = "TEXT")
    private String backText;

    @OneToMany(mappedBy = "flashcard", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FlashcardImage> images;

    @Column(name = "created_at")
    private java.time.LocalDateTime createdAt;

    @Column(name = "updated_at")
    private java.time.LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private java.time.LocalDateTime deletedAt;
}

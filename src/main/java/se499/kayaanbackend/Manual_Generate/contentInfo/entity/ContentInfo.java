package se499.kayaanbackend.Manual_Generate.contentInfo.entity;

import jakarta.persistence.*;
import lombok.*;
import se499.kayaanbackend.Manual_Generate.Flashcard.entity.Flashcard;
import se499.kayaanbackend.Manual_Generate.Note.entity.Note;
import se499.kayaanbackend.Manual_Generate.Quiz.entity.Quiz;
import se499.kayaanbackend.security.entity.User;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "content_info")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "content_id")
    private Long contentId;

    @Column(name = "title", nullable = false)
    private String title;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "difficulty", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentDifficulty difficulty;

    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @ElementCollection
    @CollectionTable(name = "content_tags", joinColumns = @JoinColumn(name = "content_id"))
    @Column(name = "tag")
    private List<String> tags;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "created_by", nullable = false)
    private User createdBy;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // One-to-one relationships with specific content types
    @OneToOne(mappedBy = "contentInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Note note;

    @OneToOne(mappedBy = "contentInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Quiz quiz;

    @OneToOne(mappedBy = "contentInfo", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Flashcard flashcard;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (isActive == null) {
            isActive = true;
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum ContentDifficulty {
        EASY, MEDIUM, HARD
    }

    public enum ContentType {
        NOTE, FLASHCARD, QUIZ
    }
}
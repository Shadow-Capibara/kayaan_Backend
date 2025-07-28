package se499.kayaanbackend.Manual_Generate.contentInfo.entity;

import jakarta.persistence.*;
import lombok.*;
import se499.kayaanbackend.Manual_Generate.Flashcard.entity.Flashcard;
import se499.kayaanbackend.Manual_Generate.Note.entity.Note;
import se499.kayaanbackend.Manual_Generate.Quiz.entity.Quiz;
import se499.kayaanbackend.redesign.entity.UserNew;
import se499.kayaanbackend.security.user.User;

import java.time.LocalDateTime;

@Entity
@Table(name = "CONTENT_INFO")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentInfo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "contentId")
    private Integer contentId;

    @Column(name = "contentTitle",nullable = false)
    private String contentTitle;

    @Column(name = "contentTag",length = 500, nullable = false)
    private String contentTag;

    @Column(name = "contentSubject", length = 100, nullable = false)
    private String contentSubject;

    @Enumerated(EnumType.STRING)
    @Column(name = "contentDifficulty",nullable = false)
    private ContentDifficulty contentDifficulty;

    @Column(name = "contentType", nullable = false, length = 50)
    private String contentType;

   @ManyToOne(fetch = FetchType.LAZY)
   @JoinColumn(name = "user_created", nullable = false)
   private User userCreated;

    @Column(name = "created_at")
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    @Builder.Default
    private LocalDateTime updatedAt = LocalDateTime.now();

    @OneToOne(mappedBy = "content", cascade = CascadeType.ALL)
    private Note note;

    @OneToOne(mappedBy = "content", cascade = CascadeType.ALL)
    private Quiz quiz;

    @OneToOne(mappedBy = "content", cascade = CascadeType.ALL)
    private Flashcard flashcard;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;

    public enum ContentDifficulty { Easy, Medium, Hard }
}

package se499.kayaanbackend.redesign.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "content_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContentInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer contentInfoID;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ContentType contentType;

    @Column(nullable = false)
    private String contentSubject;

    @Column(nullable = false)
    private String contentTitle;

    @Column(nullable = false)
    private String tag;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Difficulty difficulty;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userID", nullable = false)
    private UserNew user;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;

    public enum ContentType { Quiz, Flashcard, Note }
    public enum Difficulty { Easy, Medium, Hard }
}

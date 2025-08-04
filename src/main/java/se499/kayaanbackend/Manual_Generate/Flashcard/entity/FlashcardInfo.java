package se499.kayaanbackend.Manual_Generate.Flashcard.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se499.kayaanbackend.common.entity.ContentInformation;

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
    @JoinColumn(name = "content_infoid", nullable = false)
    private ContentInformation contentInformation;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String flashcardDetail;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String flashcardAnswer;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
}

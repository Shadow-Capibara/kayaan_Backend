package se499.kayaanbackend.Manual_Generate.Flashcard.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se499.kayaanbackend.Manual_Generate.contentInfo.entity.ContentInfo;
import se499.kayaanbackend.Study_Group.entity.Group;
import se499.kayaanbackend.common.entity.ContentInformation;

import java.time.LocalDateTime;
import java.util.List;

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
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_infoid", nullable = false)
    private ContentInfo contentInfo;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String frontText;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String backText;

    @Column(nullable = true)
    private String frontImageUrl;

    @Column(nullable = true)
    private String backImageUrl;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}

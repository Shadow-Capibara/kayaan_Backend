package se499.kayaanbackend.Manual_Generate.Flashcard.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "flashcards")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Flashcard {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String createdByUsername;

    @Column(nullable = false)
    private String frontText;   // e.g. “Define polymorphism”

    @Column(nullable = false)
    private String backText;    // e.g. “Polymorphism is ...”

    private String subject;
    private String difficulty;

    @ElementCollection
    @CollectionTable(name = "flashcard_tags", joinColumns = @JoinColumn(name = "flashcard_id"))
    @Column(name = "tag")
    private java.util.List<String> tags;
}

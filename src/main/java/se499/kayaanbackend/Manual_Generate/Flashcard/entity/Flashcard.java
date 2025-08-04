package se499.kayaanbackend.Manual_Generate.Flashcard.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import se499.kayaanbackend.Study_Group.entity.Group;

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

    private String category;

    private String frontImageUrl;

    private String backImageUrl;

    @ElementCollection
    @CollectionTable(name = "flashcard_tags", joinColumns = @JoinColumn(name = "flashcard_id"))
    @Column(name = "tag")
    private java.util.List<String> tags;

    @ManyToMany
    @JoinTable(
            name = "flashcard_shared_groups",
            joinColumns = @JoinColumn(name = "flashcard_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private java.util.List<Group> sharedGroups;
}

package se499.kayaanbackend.Manual_Generate.Note.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import se499.kayaanbackend.Manual_Generate.Group.entity.Group;

@Entity
@Table(name = "notes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String createdByUsername;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(nullable = true)
    private String subject;

    @Column(nullable = true)
    private String difficulty;

    @Column(nullable = true)
    private String category;

    @Column(nullable = true)
    private String imageUrl;

    @ManyToMany
    @JoinTable(
            name = "note_shared_groups",
            joinColumns = @JoinColumn(name = "note_id"),
            inverseJoinColumns = @JoinColumn(name = "group_id")
    )
    private List<Group> sharedGroups;

    @ElementCollection
    @CollectionTable(name = "note_tags", joinColumns = @JoinColumn(name = "note_id"))
    @Column(name = "tag")
    private List<String> tags;
}

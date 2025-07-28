package se499.kayaanbackend.Manual_Generate.Note.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import se499.kayaanbackend.Manual_Generate.Group.entity.Group;
import se499.kayaanbackend.Manual_Generate.contentInfo.entity.ContentInfo;

@Entity
@Table(name = "NOTE")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "note_id")
    private Integer noteId;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "content_id", nullable = false)
    private ContentInfo content;

    @Column(name = "note_text", nullable = false, columnDefinition = "TEXT")
    private String noteText;
}

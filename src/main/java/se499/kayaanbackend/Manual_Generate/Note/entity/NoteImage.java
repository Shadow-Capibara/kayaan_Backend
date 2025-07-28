package se499.kayaanbackend.Manual_Generate.Note.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "note_image")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer imageID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "noteID", nullable = false)
    private NoteInfo noteInformation;

    @Column(nullable = false)
    private String imageURL;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
}

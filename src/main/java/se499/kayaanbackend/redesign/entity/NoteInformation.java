package se499.kayaanbackend.redesign.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "note_information")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NoteInformation {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer noteID;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contentInfoID", nullable = false)
    private ContentInformation contentInformation;

    @Column(name = "note_text", columnDefinition = "LONGTEXT")
    private String noteText;

    private LocalDateTime created_at;
    private LocalDateTime updated_at;
    private LocalDateTime deleted_at;
}

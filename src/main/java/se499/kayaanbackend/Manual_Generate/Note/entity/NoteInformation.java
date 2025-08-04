package se499.kayaanbackend.Manual_Generate.Note.entity;

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

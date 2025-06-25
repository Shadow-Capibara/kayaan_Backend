package se499.kayaanbackend.Manual_Generate.Note.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.Manual_Generate.Note.entity.Note;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByCreatedByUsername(String username);
    List<Note> findByCreatedByUsernameAndCategory(String username, String category);
    List<Note> findByCreatedByUsernameAndSubject(String username, String subject);
}

package se499.kayaanbackend.Manual_Generate.Note.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se499.kayaanbackend.Manual_Generate.Note.entity.Note;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    Optional<Note> findByContent_ContentId(Long contentId);

    @Query("SELECT n FROM Note n JOIN n.content c WHERE c.userCreatedAt.username = :username")
    List<Note> findAllByUsername(@Param("username") String username);

//    List<Note> findByCreatedByUsername(String username);
//    List<Note> findByCreatedByUsernameAndCategory(String username, String category);
//    List<Note> findByCreatedByUsernameAndSubject(String username, String subject);
}

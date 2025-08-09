package se499.kayaanbackend.Manual_Generate.Note.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.Manual_Generate.Note.entity.Note;

import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    
    @Query("SELECT n FROM Note n WHERE n.contentInfo.createdBy.username = :username")
    List<Note> findByCreatedByUsername(@Param("username") String username);
    
    @Query("SELECT n FROM Note n WHERE n.contentInfo.createdBy.username = :username AND n.contentInfo.subject = :subject")
    List<Note> findByCreatedByUsernameAndSubject(@Param("username") String username, @Param("subject") String subject);
    
    @Query("SELECT n FROM Note n WHERE n.contentInfo.contentId = :contentId")
    Optional<Note> findByContentInfo_ContentId(@Param("contentId") Long contentId);
}

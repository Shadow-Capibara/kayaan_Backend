package se499.kayaanbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.entity.Note;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByContentInformation_User_Username(String username);
}

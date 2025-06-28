package se499.kayaanbackend.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.content.entity.NoteInformation;

public interface NoteInformationRepository extends JpaRepository<NoteInformation, Long> {
}

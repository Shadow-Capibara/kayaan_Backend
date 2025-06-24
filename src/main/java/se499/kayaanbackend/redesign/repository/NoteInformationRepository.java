package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.redesign.entity.NoteInformation;

public interface NoteInformationRepository extends JpaRepository<NoteInformation, Integer> {
}

package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.redesign.entity.NoteImage;

public interface NoteImageRepository extends JpaRepository<NoteImage, Integer> {
}

package se499.kayaanbackend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.entity.NoteImage;

public interface NoteImageRepository extends JpaRepository<NoteImage, Long> {
}

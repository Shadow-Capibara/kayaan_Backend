package se499.kayaanbackend.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.content.entity.NoteImage;

public interface NoteImageRepository extends JpaRepository<NoteImage, Long> {
}

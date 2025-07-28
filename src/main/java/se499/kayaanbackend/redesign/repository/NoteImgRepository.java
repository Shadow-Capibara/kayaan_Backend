package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.Manual_Generate.Note.entity.NoteImage;

public interface NoteImgRepository extends JpaRepository<NoteImage, Integer> {
}

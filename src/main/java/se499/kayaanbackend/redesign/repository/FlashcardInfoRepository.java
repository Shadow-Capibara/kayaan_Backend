package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.redesign.entity.FlashcardInfo;

public interface FlashcardInfoRepository extends JpaRepository<FlashcardInfo, Integer> {
}

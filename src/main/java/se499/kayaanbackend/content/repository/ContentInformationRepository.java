package se499.kayaanbackend.content.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.content.entity.ContentInformation;

import java.util.List;

public interface ContentInformationRepository extends JpaRepository<ContentInformation, Long> {
    List<ContentInformation> findByUserIdAndDeletedAtIsNull(Long userId);
}

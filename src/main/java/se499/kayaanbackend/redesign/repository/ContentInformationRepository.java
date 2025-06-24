package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.redesign.entity.ContentInformation;

public interface ContentInformationRepository extends JpaRepository<ContentInformation, Integer> {
}

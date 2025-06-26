package se499.kayaanbackend.Manual_Generate.contentInfo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.Manual_Generate.contentInfo.entity.ContentInformation;

public interface ContentInformationRepository extends JpaRepository<ContentInformation, Integer> {
}

package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.redesign.entity.GroupContent;

public interface GroupContentRepository extends JpaRepository<GroupContent, Integer> {
}

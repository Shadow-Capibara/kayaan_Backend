package se499.kayaanbackend.Manual_Generate.Group.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.Manual_Generate.Group.entity.Group;

public interface GroupRepository extends JpaRepository<Group, Long> {
}

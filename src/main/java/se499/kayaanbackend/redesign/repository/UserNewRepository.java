package se499.kayaanbackend.redesign.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import se499.kayaanbackend.redesign.entity.UserNew;

public interface UserNewRepository extends JpaRepository<UserNew, Integer> {
}

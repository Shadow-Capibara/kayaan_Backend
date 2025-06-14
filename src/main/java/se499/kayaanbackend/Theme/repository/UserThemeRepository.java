package se499.kayaanbackend.Theme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.Theme.entity.UserTheme;

import java.util.Optional;

@Repository
public interface UserThemeRepository extends JpaRepository<UserTheme, Integer> {
    Optional<UserTheme> findByUserId(Integer userId);
}
package se499.kayaanbackend.Theme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import se499.kayaanbackend.Theme.entity.Theme;

import java.util.List;
import java.util.Optional;

@Repository
public interface ThemeRepository extends JpaRepository<Theme, Long> {
    List<Theme> findByIsSystemThemeTrue();
    Optional<Theme> findByName(String name);
}
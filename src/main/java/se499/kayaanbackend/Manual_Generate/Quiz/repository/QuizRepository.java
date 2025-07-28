package se499.kayaanbackend.Manual_Generate.Quiz.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import se499.kayaanbackend.Manual_Generate.Quiz.entity.Quiz;

import java.util.List;
import java.util.Optional;

public interface QuizRepository extends JpaRepository<Quiz, Long> {
    Optional<Quiz> findByContent_ContentId(Integer contentId);

    @Query("SELECT q FROM Quiz q JOIN q.content c WHERE c.userCreatedAt.username = :username")
    List<Quiz> findAllByUsername(@Param("username") String username);

//    List<Quiz> findByCreatedByUsername(String username);
//    List<Quiz> findByCreatedByUsernameAndCategory(String username, String category);
//    List<Quiz> findByCreatedByUsernameAndQuestions_Subject(String username, String subject);
}

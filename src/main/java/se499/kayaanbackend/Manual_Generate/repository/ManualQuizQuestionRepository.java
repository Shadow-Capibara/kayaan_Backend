package se499.kayaanbackend.Manual_Generate.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Manual_Generate.entity.ManualQuizQuestion;

@Repository
public interface ManualQuizQuestionRepository extends JpaRepository<ManualQuizQuestion, Long> {
    
    @Query("SELECT q FROM ManualQuizQuestion q WHERE q.quiz.id = :quizId ORDER BY q.questionOrder ASC")
    List<ManualQuizQuestion> findByQuizIdOrderByOrder(@Param("quizId") Long quizId);
    
    @Query("SELECT q FROM ManualQuizQuestion q WHERE q.quiz.id = :quizId AND q.questionType = :type ORDER BY q.questionOrder ASC")
    List<ManualQuizQuestion> findByQuizIdAndType(@Param("quizId") Long quizId, @Param("type") ManualQuizQuestion.QuestionType type);
    
    @Query("DELETE FROM ManualQuizQuestion q WHERE q.quiz.id = :quizId")
    void deleteByQuizId(@Param("quizId") Long quizId);
}

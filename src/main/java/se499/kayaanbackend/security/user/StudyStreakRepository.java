package se499.kayaanbackend.security.user;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository for StudyStreak entity
 * Implements flowchart logic queries
 */
@Repository
public interface StudyStreakRepository extends JpaRepository<StudyStreak, Long> {
    
    /**
     * Find study streak by user ID
     * @param userId User ID
     * @return Optional StudyStreak
     */
    Optional<StudyStreak> findByUserId(Long userId);
    
    /**
     * Check if study streak exists for user
     * @param userId User ID
     * @return True if exists
     */
    boolean existsByUserId(Long userId);
    
    /**
     * Find all streaks that need daily check
     * @return List of StudyStreak
     */
    @Query("SELECT s FROM StudyStreak s WHERE s.lastActivityTime IS NULL OR s.lastActivityTime < :today")
    List<StudyStreak> findStreaksNeedingDailyCheck(@Param("today") java.time.LocalDateTime today);
    
    /**
     * Find streaks with freezing count > 1 in past week
     * @param weekAgo Date from one week ago
     * @return List of StudyStreak
     */
    @Query("SELECT s FROM StudyStreak s WHERE s.freezingCount > 1 AND s.lastFreezeDate > :weekAgo")
    List<StudyStreak> findStreaksWithMultipleFreezesInPastWeek(@Param("weekAgo") LocalDate weekAgo);
    
    /**
     * Find streaks with freezing count > 2 in current month
     * @param monthStart Start of current month
     * @return List of StudyStreak
     */
    @Query("SELECT s FROM StudyStreak s WHERE s.freezingCount > 2 AND s.lastFreezeDate > :monthStart")
    List<StudyStreak> findStreaksWithMultipleFreezesInCurrentMonth(@Param("monthStart") LocalDate monthStart);
}

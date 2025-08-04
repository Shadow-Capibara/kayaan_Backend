package se499.kayaanbackend.Study_Streak.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Study_Streak.entity.StudySession;

@Repository
public interface StudySessionRepository extends JpaRepository<StudySession, Long> {

    // Find active session for user
    Optional<StudySession> findByUserIdAndIsCompletedFalse(Integer userId);

    // Find all sessions for user
    Page<StudySession> findByUserIdOrderByStartTimeDesc(Integer userId, Pageable pageable);

    // Find sessions by date range
    @Query("SELECT s FROM StudySession s WHERE s.user.id = :userId AND DATE(s.startTime) BETWEEN :startDate AND :endDate ORDER BY s.startTime DESC")
    List<StudySession> findByUserIdAndDateRange(@Param("userId") Integer userId, 
                                               @Param("startDate") LocalDate startDate, 
                                               @Param("endDate") LocalDate endDate);

    // Find total study minutes for user on specific date
    @Query("SELECT COALESCE(SUM(s.durationMinutes), 0) FROM StudySession s WHERE s.user.id = :userId AND DATE(s.startTime) = :date AND s.isCompleted = true")
    Integer findTotalStudyMinutesByUserIdAndDate(@Param("userId") Integer userId, @Param("date") LocalDate date);

    // Find total study minutes for user in date range
    @Query("SELECT COALESCE(SUM(s.durationMinutes), 0) FROM StudySession s WHERE s.user.id = :userId AND DATE(s.startTime) BETWEEN :startDate AND :endDate AND s.isCompleted = true")
    Integer findTotalStudyMinutesByUserIdAndDateRange(@Param("userId") Integer userId, 
                                                     @Param("startDate") LocalDate startDate, 
                                                     @Param("endDate") LocalDate endDate);

    // Check if user has studied on specific date
    @Query("SELECT COUNT(s) > 0 FROM StudySession s WHERE s.user.id = :userId AND DATE(s.startTime) = :date AND s.isCompleted = true AND s.durationMinutes >= 5")
    boolean hasStudiedOnDate(@Param("userId") Integer userId, @Param("date") LocalDate date);

    // Find last study date for user
    @Query("SELECT MAX(DATE(s.startTime)) FROM StudySession s WHERE s.user.id = :userId AND s.isCompleted = true")
    Optional<LocalDate> findLastStudyDate(@Param("userId") Integer userId);

    // Find study sessions by subject
    @Query("SELECT s FROM StudySession s WHERE s.user.id = :userId AND s.subject = :subject AND s.isCompleted = true ORDER BY s.startTime DESC")
    List<StudySession> findByUserIdAndSubject(@Param("userId") Integer userId, @Param("subject") String subject);

    // Count total study days for user
    @Query("SELECT COUNT(DISTINCT DATE(s.startTime)) FROM StudySession s WHERE s.user.id = :userId AND s.isCompleted = true AND s.durationMinutes >= 5")
    Integer countTotalStudyDays(@Param("userId") Integer userId);

    // Find sessions that exceed daily limit (8 hours = 480 minutes)
    @Query("SELECT s FROM StudySession s WHERE s.user.id = :userId AND DATE(s.startTime) = :date AND s.durationMinutes > 480")
    List<StudySession> findSessionsExceedingDailyLimit(@Param("userId") Integer userId, @Param("date") LocalDate date);
} 
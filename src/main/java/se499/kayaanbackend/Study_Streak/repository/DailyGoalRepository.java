package se499.kayaanbackend.Study_Streak.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Study_Streak.entity.DailyGoal;

@Repository
public interface DailyGoalRepository extends JpaRepository<DailyGoal, Long> {

    // Find daily goal by user ID and date
    Optional<DailyGoal> findByUserIdAndGoalDate(Integer userId, LocalDate goalDate);

    // Find all daily goals for user
    List<DailyGoal> findByUserIdOrderByGoalDateDesc(Integer userId);

    // Find daily goals by date range
    @Query("SELECT dg FROM DailyGoal dg WHERE dg.user.id = :userId AND dg.goalDate BETWEEN :startDate AND :endDate ORDER BY dg.goalDate DESC")
    List<DailyGoal> findByUserIdAndDateRange(@Param("userId") Integer userId, 
                                            @Param("startDate") LocalDate startDate, 
                                            @Param("endDate") LocalDate endDate);

    // Find achieved goals for user
    List<DailyGoal> findByUserIdAndIsAchievedTrueOrderByGoalDateDesc(Integer userId);

    // Find unachieved goals for user
    List<DailyGoal> findByUserIdAndIsAchievedFalseOrderByGoalDateDesc(Integer userId);

    // Count achieved goals for user
    @Query("SELECT COUNT(dg) FROM DailyGoal dg WHERE dg.user.id = :userId AND dg.isAchieved = true")
    Integer countAchievedGoals(@Param("userId") Integer userId);

    // Count total goals for user
    @Query("SELECT COUNT(dg) FROM DailyGoal dg WHERE dg.user.id = :userId")
    Integer countTotalGoals(@Param("userId") Integer userId);

    // Find current daily goal (today)
    @Query("SELECT dg FROM DailyGoal dg WHERE dg.user.id = :userId AND dg.goalDate = :date")
    Optional<DailyGoal> findCurrentDailyGoal(@Param("userId") Integer userId, @Param("date") LocalDate date);

    // Check if daily goal exists for date
    boolean existsByUserIdAndGoalDate(Integer userId, LocalDate goalDate);
} 
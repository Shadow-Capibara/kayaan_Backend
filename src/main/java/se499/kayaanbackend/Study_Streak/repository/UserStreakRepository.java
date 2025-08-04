package se499.kayaanbackend.Study_Streak.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import se499.kayaanbackend.Study_Streak.entity.UserStreak;

@Repository
public interface UserStreakRepository extends JpaRepository<UserStreak, Long> {

    // Find streak by user ID
    Optional<UserStreak> findByUserId(Integer userId);

    // Find top streaks for leaderboard
    @Query("SELECT us FROM UserStreak us ORDER BY us.currentStreak DESC, us.longestStreak DESC")
    List<UserStreak> findTopStreaks();

    // Find users with longest streaks
    @Query("SELECT us FROM UserStreak us ORDER BY us.longestStreak DESC")
    List<UserStreak> findUsersWithLongestStreaks();

    // Find users with most study minutes
    @Query("SELECT us FROM UserStreak us ORDER BY us.totalStudyMinutes DESC")
    List<UserStreak> findUsersWithMostStudyMinutes();

    // Find users with most study days
    @Query("SELECT us FROM UserStreak us ORDER BY us.totalStudyDays DESC")
    List<UserStreak> findUsersWithMostStudyDays();

    // Check if user streak exists
    boolean existsByUserId(Integer userId);
} 
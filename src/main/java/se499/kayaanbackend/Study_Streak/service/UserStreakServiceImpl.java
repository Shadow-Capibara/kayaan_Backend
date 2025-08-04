package se499.kayaanbackend.Study_Streak.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.Study_Streak.dto.UserStreakResponseDTO;
import se499.kayaanbackend.Study_Streak.entity.UserStreak;
import se499.kayaanbackend.Study_Streak.repository.UserStreakRepository;
import se499.kayaanbackend.common.exception.ResourceNotFoundException;
import se499.kayaanbackend.security.entity.User;
import se499.kayaanbackend.security.entity.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserStreakServiceImpl implements UserStreakService {

    private final UserStreakRepository userStreakRepository;
    private final UserRepository userRepository;
    private final StudySessionService studySessionService;

    @Override
    public UserStreakResponseDTO getCurrentStreak(Integer userId) {
        log.info("Getting current streak for user: {}", userId);

        UserStreak userStreak = userStreakRepository.findByUserId(userId)
                .orElseGet(() -> initializeStreakForUser(userId));

        return mapToResponseDTO(userStreak);
    }

    @Override
    public void updateStreakAfterSession(Integer userId, Integer studyMinutes) {
        log.info("Updating streak for user: {} after session with {} minutes", userId, studyMinutes);

        UserStreak userStreak = userStreakRepository.findByUserId(userId)
                .orElseGet(() -> initializeStreakForUser(userId));

        LocalDate today = LocalDate.now();

        // Add study minutes
        userStreak.addStudyMinutes(studyMinutes);

        // Check if user studied today
        if (studySessionService.hasStudiedOnDate(userId, today)) {
            // If this is the first study session of the day
            if (userStreak.getLastStudyDate() == null || !userStreak.getLastStudyDate().equals(today)) {
                userStreak.incrementStudyDays();
                userStreak.setLastStudyDate(today);

                // Check if this continues the streak
                if (userStreak.getLastStudyDate() != null) {
                    LocalDate yesterday = today.minusDays(1);
                    if (userStreak.getLastStudyDate().equals(yesterday)) {
                        // Continue streak
                        userStreak.incrementStreak();
                        log.info("Streak continued for user: {}. Current streak: {}", userId, userStreak.getCurrentStreak());
                    } else if (userStreak.getLastStudyDate().isBefore(yesterday)) {
                        // Break in streak, start new streak
                        userStreak.setCurrentStreak(1);
                        log.info("New streak started for user: {}", userId);
                    } else {
                        // Same day, don't change streak
                        log.info("Same day study session for user: {}", userId);
                    }
                } else {
                    // First study session ever
                    userStreak.setCurrentStreak(1);
                    log.info("First study session for user: {}. Streak started.", userId);
                }
            }
        }

        userStreakRepository.save(userStreak);
        log.info("Streak updated for user: {}. Current streak: {}, Total minutes: {}", 
                userId, userStreak.getCurrentStreak(), userStreak.getTotalStudyMinutes());
    }

    @Override
    public UserStreakResponseDTO getStreakStatistics(Integer userId) {
        return getCurrentStreak(userId);
    }

    @Override
    public List<UserStreakResponseDTO> getLeaderboard() {
        log.info("Getting streak leaderboard");

        List<UserStreak> topStreaks = userStreakRepository.findTopStreaks();
        return topStreaks.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void initializeStreak(Integer userId) {
        log.info("Initializing streak for user: {}", userId);

        if (!userStreakRepository.existsByUserId(userId)) {
            initializeStreakForUser(userId);
        }
    }

    @Override
    public void resetStreak(Integer userId) {
        log.info("Resetting streak for user: {}", userId);

        UserStreak userStreak = userStreakRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User streak not found"));

        userStreak.resetStreak();
        userStreakRepository.save(userStreak);
    }

    @Override
    public void checkAndUpdateStreak(Integer userId) {
        log.info("Checking and updating streak for user: {}", userId);

        UserStreak userStreak = userStreakRepository.findByUserId(userId)
                .orElseGet(() -> initializeStreakForUser(userId));

        LocalDate today = LocalDate.now();
        LocalDate lastStudyDate = userStreak.getLastStudyDate();

        if (lastStudyDate != null) {
            // Check if streak should be reset (missed yesterday)
            LocalDate yesterday = today.minusDays(1);
            if (lastStudyDate.isBefore(yesterday)) {
                userStreak.resetStreak();
                userStreakRepository.save(userStreak);
                log.info("Streak reset for user: {} due to missed day", userId);
            }
        }
    }

    private UserStreak initializeStreakForUser(Integer userId) {
        log.info("Initializing new streak for user: {}", userId);

        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        UserStreak userStreak = UserStreak.builder()
                .user(user)
                .currentStreak(0)
                .longestStreak(0)
                .totalStudyDays(0)
                .totalStudyMinutes(0)
                .build();

        return userStreakRepository.save(userStreak);
    }

    private UserStreakResponseDTO mapToResponseDTO(UserStreak userStreak) {
        return UserStreakResponseDTO.builder()
                .id(userStreak.getId())
                .userId(userStreak.getUser().getId().longValue())
                .username(userStreak.getUser().getUsername())
                .currentStreak(userStreak.getCurrentStreak())
                .longestStreak(userStreak.getLongestStreak())
                .lastStudyDate(userStreak.getLastStudyDate())
                .totalStudyDays(userStreak.getTotalStudyDays())
                .totalStudyMinutes(userStreak.getTotalStudyMinutes())
                .totalStudyHours(userStreak.getTotalStudyMinutes() / 60)
                .build();
    }
} 
package se499.kayaanbackend.Study_Streak.service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.Study_Streak.dto.DailyGoalRequestDTO;
import se499.kayaanbackend.Study_Streak.dto.DailyGoalResponseDTO;
import se499.kayaanbackend.Study_Streak.entity.DailyGoal;
import se499.kayaanbackend.Study_Streak.repository.DailyGoalRepository;
import se499.kayaanbackend.common.exception.ResourceNotFoundException;
import se499.kayaanbackend.common.exception.UnauthorizedException;
import se499.kayaanbackend.security.entity.User;
import se499.kayaanbackend.security.entity.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class DailyGoalServiceImpl implements DailyGoalService {

    private final DailyGoalRepository dailyGoalRepository;
    private final UserRepository userRepository;
    private final StudySessionService studySessionService;

    @Override
    public DailyGoalResponseDTO createDailyGoal(Integer userId, DailyGoalRequestDTO requestDTO) {
        log.info("Creating daily goal for user: {} with target: {} minutes", userId, requestDTO.getTargetMinutes());

        // Check if goal already exists for today
        LocalDate today = LocalDate.now();
        if (dailyGoalRepository.existsByUserIdAndGoalDate(userId, today)) {
            throw new IllegalStateException("Daily goal already exists for today");
        }

        // Get user
        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create daily goal
        DailyGoal dailyGoal = DailyGoal.builder()
                .user(user)
                .targetMinutes(requestDTO.getTargetMinutes())
                .goalDate(today)
                .achievedMinutes(0)
                .isAchieved(false)
                .build();

        DailyGoal savedGoal = dailyGoalRepository.save(dailyGoal);
        log.info("Daily goal created with ID: {}", savedGoal.getId());

        return mapToResponseDTO(savedGoal);
    }

    @Override
    public DailyGoalResponseDTO getCurrentDailyGoal(Integer userId) {
        log.info("Getting current daily goal for user: {}", userId);

        LocalDate today = LocalDate.now();
        DailyGoal dailyGoal = dailyGoalRepository.findCurrentDailyGoal(userId, today)
                .orElse(null);

        if (dailyGoal == null) {
            // Return default goal with 0 progress
            return DailyGoalResponseDTO.builder()
                    .userId(userId.longValue())
                    .targetMinutes(0)
                    .goalDate(today)
                    .achievedMinutes(0)
                    .isAchieved(false)
                    .progressPercentage(0.0)
                    .build();
        }

        return mapToResponseDTO(dailyGoal);
    }

    @Override
    public DailyGoalResponseDTO updateDailyGoal(Integer userId, Long goalId, DailyGoalRequestDTO requestDTO) {
        log.info("Updating daily goal: {} for user: {}", goalId, userId);

        DailyGoal dailyGoal = dailyGoalRepository.findById(goalId)
                .orElseThrow(() -> new ResourceNotFoundException("Daily goal not found"));

        // Check ownership
        if (!dailyGoal.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to update this goal");
        }

        // Update goal
        dailyGoal.setTargetMinutes(requestDTO.getTargetMinutes());
        dailyGoal.updateAchievedMinutes(dailyGoal.getAchievedMinutes()); // Recalculate achievement status

        DailyGoal savedGoal = dailyGoalRepository.save(dailyGoal);
        return mapToResponseDTO(savedGoal);
    }

    @Override
    public List<DailyGoalResponseDTO> getDailyGoalHistory(Integer userId) {
        log.info("Getting daily goal history for user: {}", userId);

        List<DailyGoal> goals = dailyGoalRepository.findByUserIdOrderByGoalDateDesc(userId);
        return goals.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public void updateAchievedMinutes(Integer userId, Integer minutes) {
        log.info("Updating achieved minutes for user: {} with {} minutes", userId, minutes);

        LocalDate today = LocalDate.now();
        DailyGoal dailyGoal = dailyGoalRepository.findCurrentDailyGoal(userId, today)
                .orElse(null);

        if (dailyGoal != null) {
            dailyGoal.addAchievedMinutes(minutes);
            dailyGoalRepository.save(dailyGoal);
            log.info("Updated achieved minutes for goal: {}. New total: {}", dailyGoal.getId(), dailyGoal.getAchievedMinutes());
        }
    }

    @Override
    public DailyGoalResponseDTO getGoalStatistics(Integer userId) {
        return getCurrentDailyGoal(userId);
    }

    @Override
    public boolean isGoalAchieved(Integer userId, LocalDate date) {
        DailyGoal dailyGoal = dailyGoalRepository.findByUserIdAndGoalDate(userId, date)
                .orElse(null);

        return dailyGoal != null && dailyGoal.getIsAchieved();
    }

    @Override
    public double getGoalProgressPercentage(Integer userId, LocalDate date) {
        DailyGoal dailyGoal = dailyGoalRepository.findByUserIdAndGoalDate(userId, date)
                .orElse(null);

        return dailyGoal != null ? dailyGoal.getProgressPercentage() : 0.0;
    }

    private DailyGoalResponseDTO mapToResponseDTO(DailyGoal dailyGoal) {
        return DailyGoalResponseDTO.builder()
                .id(dailyGoal.getId())
                .userId(dailyGoal.getUser().getId().longValue())
                .username(dailyGoal.getUser().getUsername())
                .targetMinutes(dailyGoal.getTargetMinutes())
                .goalDate(dailyGoal.getGoalDate())
                .achievedMinutes(dailyGoal.getAchievedMinutes())
                .isAchieved(dailyGoal.getIsAchieved())
                .progressPercentage(dailyGoal.getProgressPercentage())
                .build();
    }
} 
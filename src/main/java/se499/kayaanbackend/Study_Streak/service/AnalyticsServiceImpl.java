package se499.kayaanbackend.Study_Streak.service;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.Study_Streak.dto.AnalyticsResponseDTO;
import se499.kayaanbackend.Study_Streak.entity.StudySession;
import se499.kayaanbackend.Study_Streak.repository.StudySessionRepository;
import se499.kayaanbackend.security.entity.User;
import se499.kayaanbackend.security.entity.UserRepository;

@Slf4j
@Service
@RequiredArgsConstructor
public class AnalyticsServiceImpl implements AnalyticsService {

    private final StudySessionRepository studySessionRepository;
    private final UserRepository userRepository;
    private final UserStreakService userStreakService;
    private final StudySessionService studySessionService;

    @Override
    public AnalyticsResponseDTO getDailyAnalytics(Integer userId, LocalDate date) {
        log.info("Getting daily analytics for user: {} on date: {}", userId, date);

        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get study sessions for the day
        List<StudySession> sessions = studySessionRepository.findByUserIdAndDateRange(userId, date, date);
        
        // Calculate total study minutes
        Integer totalMinutes = studySessionService.getTotalStudyMinutesForDate(userId, date);
        
        // Get subject breakdown
        Map<String, Integer> subjectBreakdown = sessions.stream()
                .filter(session -> session.getSubject() != null)
                .collect(Collectors.groupingBy(
                    StudySession::getSubject,
                    Collectors.summingInt(session -> session.getDurationMinutes() != null ? session.getDurationMinutes() : 0)
                ));

        // Get streak info
        var streakInfo = userStreakService.getCurrentStreak(userId);

        return AnalyticsResponseDTO.builder()
                .userId(userId.longValue())
                .username(user.getUsername())
                .totalStudyMinutes(totalMinutes)
                .totalStudyHours(totalMinutes / 60)
                .totalStudyDays(streakInfo.getTotalStudyDays())
                .currentStreak(streakInfo.getCurrentStreak())
                .longestStreak(streakInfo.getLongestStreak())
                .averageMinutesPerDay(calculateAverageMinutesPerDay(userId))
                .subjectBreakdown(subjectBreakdown)
                .build();
    }

    @Override
    public AnalyticsResponseDTO getWeeklyAnalytics(Integer userId, LocalDate startDate) {
        log.info("Getting weekly analytics for user: {} starting from: {}", userId, startDate);

        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate endDate = startDate.plusDays(6);
        
        // Get study sessions for the week
        List<StudySession> sessions = studySessionRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        
        // Calculate total study minutes
        Integer totalMinutes = studySessionService.getTotalStudyMinutesForDateRange(userId, startDate, endDate);
        
        // Get subject breakdown
        Map<String, Integer> subjectBreakdown = sessions.stream()
                .filter(session -> session.getSubject() != null)
                .collect(Collectors.groupingBy(
                    StudySession::getSubject,
                    Collectors.summingInt(session -> session.getDurationMinutes() != null ? session.getDurationMinutes() : 0)
                ));

        // Get streak info
        var streakInfo = userStreakService.getCurrentStreak(userId);

        return AnalyticsResponseDTO.builder()
                .userId(userId.longValue())
                .username(user.getUsername())
                .totalStudyMinutes(totalMinutes)
                .totalStudyHours(totalMinutes / 60)
                .totalStudyDays(streakInfo.getTotalStudyDays())
                .currentStreak(streakInfo.getCurrentStreak())
                .longestStreak(streakInfo.getLongestStreak())
                .averageMinutesPerDay(calculateAverageMinutesPerDay(userId))
                .subjectBreakdown(subjectBreakdown)
                .build();
    }

    @Override
    public AnalyticsResponseDTO getMonthlyAnalytics(Integer userId, String yearMonth) {
        log.info("Getting monthly analytics for user: {} for month: {}", userId, yearMonth);

        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("User not found"));

        YearMonth ym = YearMonth.parse(yearMonth, DateTimeFormatter.ofPattern("yyyy-MM"));
        LocalDate startDate = ym.atDay(1);
        LocalDate endDate = ym.atEndOfMonth();
        
        // Get study sessions for the month
        List<StudySession> sessions = studySessionRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        
        // Calculate total study minutes
        Integer totalMinutes = studySessionService.getTotalStudyMinutesForDateRange(userId, startDate, endDate);
        
        // Get subject breakdown
        Map<String, Integer> subjectBreakdown = sessions.stream()
                .filter(session -> session.getSubject() != null)
                .collect(Collectors.groupingBy(
                    StudySession::getSubject,
                    Collectors.summingInt(session -> session.getDurationMinutes() != null ? session.getDurationMinutes() : 0)
                ));

        // Get streak info
        var streakInfo = userStreakService.getCurrentStreak(userId);

        return AnalyticsResponseDTO.builder()
                .userId(userId.longValue())
                .username(user.getUsername())
                .totalStudyMinutes(totalMinutes)
                .totalStudyHours(totalMinutes / 60)
                .totalStudyDays(streakInfo.getTotalStudyDays())
                .currentStreak(streakInfo.getCurrentStreak())
                .longestStreak(streakInfo.getLongestStreak())
                .averageMinutesPerDay(calculateAverageMinutesPerDay(userId))
                .subjectBreakdown(subjectBreakdown)
                .build();
    }

    @Override
    public AnalyticsResponseDTO getStudyTrends(Integer userId, Integer days) {
        log.info("Getting study trends for user: {} for {} days", userId, days);

        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("User not found"));

        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(days - 1);
        
        // Get study sessions for the period
        List<StudySession> sessions = studySessionRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        
        // Calculate total study minutes
        Integer totalMinutes = studySessionService.getTotalStudyMinutesForDateRange(userId, startDate, endDate);
        
        // Get subject breakdown
        Map<String, Integer> subjectBreakdown = sessions.stream()
                .filter(session -> session.getSubject() != null)
                .collect(Collectors.groupingBy(
                    StudySession::getSubject,
                    Collectors.summingInt(session -> session.getDurationMinutes() != null ? session.getDurationMinutes() : 0)
                ));

        // Get streak info
        var streakInfo = userStreakService.getCurrentStreak(userId);

        return AnalyticsResponseDTO.builder()
                .userId(userId.longValue())
                .username(user.getUsername())
                .totalStudyMinutes(totalMinutes)
                .totalStudyHours(totalMinutes / 60)
                .totalStudyDays(streakInfo.getTotalStudyDays())
                .currentStreak(streakInfo.getCurrentStreak())
                .longestStreak(streakInfo.getLongestStreak())
                .averageMinutesPerDay(calculateAverageMinutesPerDay(userId))
                .subjectBreakdown(subjectBreakdown)
                .build();
    }

    @Override
    public AnalyticsResponseDTO getStudySummary(Integer userId) {
        log.info("Getting study summary for user: {}", userId);

        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new RuntimeException("User not found"));

        // Get streak info
        var streakInfo = userStreakService.getCurrentStreak(userId);

        return AnalyticsResponseDTO.builder()
                .userId(userId.longValue())
                .username(user.getUsername())
                .totalStudyMinutes(streakInfo.getTotalStudyMinutes())
                .totalStudyHours(streakInfo.getTotalStudyHours())
                .totalStudyDays(streakInfo.getTotalStudyDays())
                .currentStreak(streakInfo.getCurrentStreak())
                .longestStreak(streakInfo.getLongestStreak())
                .averageMinutesPerDay(calculateAverageMinutesPerDay(userId))
                .build();
    }

    private Double calculateAverageMinutesPerDay(Integer userId) {
        var streakInfo = userStreakService.getCurrentStreak(userId);
        if (streakInfo.getTotalStudyDays() == 0) {
            return 0.0;
        }
        return (double) streakInfo.getTotalStudyMinutes() / streakInfo.getTotalStudyDays();
    }
} 
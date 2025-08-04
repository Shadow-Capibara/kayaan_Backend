package se499.kayaanbackend.Study_Streak.service;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import se499.kayaanbackend.Study_Streak.dto.StudySessionRequestDTO;
import se499.kayaanbackend.Study_Streak.dto.StudySessionResponseDTO;

public interface StudySessionService {

    // Start a new study session
    StudySessionResponseDTO startSession(Integer userId, StudySessionRequestDTO requestDTO);

    // End an active study session
    StudySessionResponseDTO endSession(Integer userId, Long sessionId);

    // Get current active session for user
    StudySessionResponseDTO getCurrentSession(Integer userId);

    // Get session history for user
    Page<StudySessionResponseDTO> getSessionHistory(Integer userId, Pageable pageable);

    // Get sessions by date range
    List<StudySessionResponseDTO> getSessionsByDateRange(Integer userId, LocalDate startDate, LocalDate endDate);

    // Update session
    StudySessionResponseDTO updateSession(Integer userId, Long sessionId, StudySessionRequestDTO requestDTO);

    // Delete session
    void deleteSession(Integer userId, Long sessionId);

    // Get total study minutes for user on specific date
    Integer getTotalStudyMinutesForDate(Integer userId, LocalDate date);

    // Get total study minutes for user in date range
    Integer getTotalStudyMinutesForDateRange(Integer userId, LocalDate startDate, LocalDate endDate);

    // Check if user has studied on specific date
    boolean hasStudiedOnDate(Integer userId, LocalDate date);

    // Get last study date for user
    LocalDate getLastStudyDate(Integer userId);

    // Validate session duration
    boolean isValidSessionDuration(Integer durationMinutes);

    // Check daily study limit
    boolean isWithinDailyLimit(Integer userId, LocalDate date, Integer additionalMinutes);
} 
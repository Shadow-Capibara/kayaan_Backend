package se499.kayaanbackend.Study_Streak.service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import se499.kayaanbackend.Study_Streak.dto.StudySessionRequestDTO;
import se499.kayaanbackend.Study_Streak.dto.StudySessionResponseDTO;
import se499.kayaanbackend.Study_Streak.entity.StudySession;
import se499.kayaanbackend.Study_Streak.repository.StudySessionRepository;
import se499.kayaanbackend.common.exception.ResourceNotFoundException;
import se499.kayaanbackend.common.exception.UnauthorizedException;
import se499.kayaanbackend.security.entity.User;
import se499.kayaanbackend.security.entity.UserRepository;

@Slf4j
@Service
@Transactional
public class StudySessionServiceImpl implements StudySessionService {

    private final StudySessionRepository studySessionRepository;
    private final UserRepository userRepository;
    private final UserStreakService userStreakService;

    public StudySessionServiceImpl(StudySessionRepository studySessionRepository, 
                                 UserRepository userRepository, 
                                 @Lazy UserStreakService userStreakService) {
        this.studySessionRepository = studySessionRepository;
        this.userRepository = userRepository;
        this.userStreakService = userStreakService;
    }

    @Override
    public StudySessionResponseDTO startSession(Integer userId, StudySessionRequestDTO requestDTO) {
        log.info("Starting study session for user: {}", userId);

        // Check if user has active session
        if (studySessionRepository.findByUserIdAndIsCompletedFalse(userId).isPresent()) {
            throw new IllegalStateException("User already has an active study session");
        }

        // Get user
        User user = userRepository.findById(userId.longValue())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Create new session
        StudySession session = StudySession.builder()
                .user(user)
                .startTime(LocalDateTime.now())
                .subject(requestDTO.getSubject())
                .sessionType(requestDTO.getSessionType())
                .isCompleted(false)
                .build();

        StudySession savedSession = studySessionRepository.save(session);
        log.info("Study session started with ID: {}", savedSession.getId());

        return mapToResponseDTO(savedSession);
    }

    @Override
    public StudySessionResponseDTO endSession(Integer userId, Long sessionId) {
        log.info("Ending study session: {} for user: {}", sessionId, userId);

        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study session not found"));

        // Check ownership
        if (!session.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to end this session");
        }

        // Check if session is already completed
        if (session.getIsCompleted()) {
            throw new IllegalStateException("Session is already completed");
        }

        // End session
        session.setEndTime(LocalDateTime.now());
        session.calculateDuration();
        session.setIsCompleted(true);

        // Validate session duration
        if (!isValidSessionDuration(session.getDurationMinutes())) {
            throw new IllegalStateException("Session duration must be at least 5 minutes");
        }

        StudySession savedSession = studySessionRepository.save(session);

        // Update user streak
        userStreakService.updateStreakAfterSession(userId, session.getDurationMinutes());

        log.info("Study session ended. Duration: {} minutes", savedSession.getDurationMinutes());

        return mapToResponseDTO(savedSession);
    }

    @Override
    public StudySessionResponseDTO getCurrentSession(Integer userId) {
        log.info("Getting current session for user: {}", userId);

        StudySession session = studySessionRepository.findByUserIdAndIsCompletedFalse(userId)
                .orElse(null);

        return session != null ? mapToResponseDTO(session) : null;
    }

    @Override
    public Page<StudySessionResponseDTO> getSessionHistory(Integer userId, Pageable pageable) {
        log.info("Getting session history for user: {}", userId);

        Page<StudySession> sessions = studySessionRepository.findByUserIdOrderByStartTimeDesc(userId, pageable);
        return sessions.map(this::mapToResponseDTO);
    }

    @Override
    public List<StudySessionResponseDTO> getSessionsByDateRange(Integer userId, LocalDate startDate, LocalDate endDate) {
        log.info("Getting sessions for user: {} from {} to {}", userId, startDate, endDate);

        List<StudySession> sessions = studySessionRepository.findByUserIdAndDateRange(userId, startDate, endDate);
        return sessions.stream()
                .map(this::mapToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public StudySessionResponseDTO updateSession(Integer userId, Long sessionId, StudySessionRequestDTO requestDTO) {
        log.info("Updating study session: {} for user: {}", sessionId, userId);

        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study session not found"));

        // Check ownership
        if (!session.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to update this session");
        }

        // Update session
        session.setSubject(requestDTO.getSubject());
        session.setSessionType(requestDTO.getSessionType());

        StudySession savedSession = studySessionRepository.save(session);
        return mapToResponseDTO(savedSession);
    }

    @Override
    public void deleteSession(Integer userId, Long sessionId) {
        log.info("Deleting study session: {} for user: {}", sessionId, userId);

        StudySession session = studySessionRepository.findById(sessionId)
                .orElseThrow(() -> new ResourceNotFoundException("Study session not found"));

        // Check ownership
        if (!session.getUser().getId().equals(userId)) {
            throw new UnauthorizedException("Not authorized to delete this session");
        }

        studySessionRepository.delete(session);
        log.info("Study session deleted successfully");
    }

    @Override
    public Integer getTotalStudyMinutesForDate(Integer userId, LocalDate date) {
        return studySessionRepository.findTotalStudyMinutesByUserIdAndDate(userId, date);
    }

    @Override
    public Integer getTotalStudyMinutesForDateRange(Integer userId, LocalDate startDate, LocalDate endDate) {
        return studySessionRepository.findTotalStudyMinutesByUserIdAndDateRange(userId, startDate, endDate);
    }

    @Override
    public boolean hasStudiedOnDate(Integer userId, LocalDate date) {
        return studySessionRepository.hasStudiedOnDate(userId, date);
    }

    @Override
    public LocalDate getLastStudyDate(Integer userId) {
        return studySessionRepository.findLastStudyDate(userId).orElse(null);
    }

    @Override
    public boolean isValidSessionDuration(Integer durationMinutes) {
        return durationMinutes != null && durationMinutes >= 5;
    }

    @Override
    public boolean isWithinDailyLimit(Integer userId, LocalDate date, Integer additionalMinutes) {
        Integer currentTotal = getTotalStudyMinutesForDate(userId, date);
        return (currentTotal + additionalMinutes) <= 480; // 8 hours = 480 minutes
    }

    private StudySessionResponseDTO mapToResponseDTO(StudySession session) {
        return StudySessionResponseDTO.builder()
                .id(session.getId())
                .userId(session.getUser().getId().longValue())
                .username(session.getUser().getUsername())
                .startTime(session.getStartTime())
                .endTime(session.getEndTime())
                .durationMinutes(session.getDurationMinutes())
                .subject(session.getSubject())
                .sessionType(session.getSessionType())
                .isCompleted(session.getIsCompleted())
                .createdAt(session.getCreatedAt())
                .updatedAt(session.getUpdatedAt())
                .build();
    }
} 
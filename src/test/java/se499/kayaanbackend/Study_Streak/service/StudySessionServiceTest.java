package se499.kayaanbackend.Study_Streak.service;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import se499.kayaanbackend.Study_Streak.dto.StudySessionRequestDTO;
import se499.kayaanbackend.Study_Streak.dto.StudySessionResponseDTO;
import se499.kayaanbackend.Study_Streak.entity.StudySession;
import se499.kayaanbackend.Study_Streak.repository.StudySessionRepository;
import se499.kayaanbackend.common.exception.ResourceNotFoundException;
import se499.kayaanbackend.security.entity.User;
import se499.kayaanbackend.security.entity.UserRepository;

@ExtendWith(MockitoExtension.class)
class StudySessionServiceTest {

    @Mock
    private StudySessionRepository studySessionRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserStreakService userStreakService;

    @InjectMocks
    private StudySessionServiceImpl studySessionService;

    private User testUser;
    private StudySession testSession;
    private StudySessionRequestDTO testRequestDTO;

    @BeforeEach
    void setUp() {
        testUser = User.builder()
                .id(1)
                .username("testuser")
                .email("test@example.com")
                .build();

        testSession = StudySession.builder()
                .id(1L)
                .user(testUser)
                .startTime(LocalDateTime.now())
                .subject("Mathematics")
                .sessionType("Study")
                .isCompleted(false)
                .build();

        testRequestDTO = StudySessionRequestDTO.builder()
                .subject("Mathematics")
                .sessionType("Study")
                .build();
    }

    @Test
    void startSession_Success() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(testUser));
        when(studySessionRepository.findByUserIdAndIsCompletedFalse(1)).thenReturn(Optional.empty());
        when(studySessionRepository.save(any(StudySession.class))).thenReturn(testSession);

        // Act
        StudySessionResponseDTO result = studySessionService.startSession(1, testRequestDTO);

        // Assert
        assertNotNull(result);
        assertEquals(testSession.getId(), result.getId());
        assertEquals(testSession.getSubject(), result.getSubject());
        verify(studySessionRepository).save(any(StudySession.class));
    }

    @Test
    void startSession_UserNotFound() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studySessionService.startSession(1, testRequestDTO);
        });
    }

    @Test
    void startSession_ActiveSessionExists() {
        // Arrange
        when(studySessionRepository.findByUserIdAndIsCompletedFalse(1)).thenReturn(Optional.of(testSession));

        // Act & Assert
        assertThrows(IllegalStateException.class, () -> {
            studySessionService.startSession(1, testRequestDTO);
        });
    }

    @Test
    void endSession_Success() {
        // Arrange
        StudySession completedSession = StudySession.builder()
                .id(1L)
                .user(testUser)
                .startTime(LocalDateTime.now().minusHours(1))
                .endTime(LocalDateTime.now())
                .durationMinutes(60)
                .subject("Mathematics")
                .sessionType("Study")
                .isCompleted(true)
                .build();

        when(studySessionRepository.findById(1L)).thenReturn(Optional.of(testSession));
        when(studySessionRepository.save(any(StudySession.class))).thenReturn(completedSession);

        // Act
        StudySessionResponseDTO result = studySessionService.endSession(1, 1L);

        // Assert
        assertNotNull(result);
        assertTrue(result.getIsCompleted());
        verify(userStreakService).updateStreakAfterSession(1, completedSession.getDurationMinutes());
    }

    @Test
    void endSession_SessionNotFound() {
        // Arrange
        when(studySessionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class, () -> {
            studySessionService.endSession(1, 1L);
        });
    }

    @Test
    void endSession_Unauthorized() {
        // Arrange
        User otherUser = User.builder().id(2).build();
        StudySession otherSession = StudySession.builder()
                .id(1L)
                .user(otherUser)
                .build();

        when(studySessionRepository.findById(1L)).thenReturn(Optional.of(otherSession));

        // Act & Assert
        assertThrows(RuntimeException.class, () -> {
            studySessionService.endSession(1, 1L);
        });
    }

    @Test
    void isValidSessionDuration_Valid() {
        // Act & Assert
        assertTrue(studySessionService.isValidSessionDuration(10));
        assertTrue(studySessionService.isValidSessionDuration(60));
    }

    @Test
    void isValidSessionDuration_Invalid() {
        // Act & Assert
        assertFalse(studySessionService.isValidSessionDuration(3));
        assertFalse(studySessionService.isValidSessionDuration(null));
    }

    @Test
    void isWithinDailyLimit_WithinLimit() {
        // Arrange
        when(studySessionRepository.findTotalStudyMinutesByUserIdAndDate(1, any())).thenReturn(400);

        // Act & Assert
        assertTrue(studySessionService.isWithinDailyLimit(1, LocalDateTime.now().toLocalDate(), 50));
    }

    @Test
    void isWithinDailyLimit_ExceedsLimit() {
        // Arrange
        when(studySessionRepository.findTotalStudyMinutesByUserIdAndDate(1, any())).thenReturn(450);

        // Act & Assert
        assertFalse(studySessionService.isWithinDailyLimit(1, LocalDateTime.now().toLocalDate(), 50));
    }
} 
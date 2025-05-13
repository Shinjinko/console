package com.console.app.main.service;

import com.console.app.main.exceptions.SessionNotFoundException;
import com.console.app.main.model.Session;
import com.console.app.main.model.User;
import com.console.app.main.repository.ConsoleRepository;
import com.console.app.main.repository.SessionRepository;
import com.console.app.main.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SessionServiceTest {

    @Mock
    private SessionRepository sessionRepository;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private SessionService sessionService;

    @Mock
    private ConsoleRepository consoleRepository;

    @Test
    void createSession_ShouldCreateNewSession() {
        // Arrange
        User user = new User();
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(sessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Session session = sessionService.createSession("ACTIVE", 1L);

        // Assert
        assertNotNull(session);
        assertEquals("ACTIVE", session.getStatus());
        assertEquals(user, session.getUser());
        assertNotNull(session.getStartTime());
        verify(sessionRepository, times(1)).save(any());
    }

    @Test
    void updateSessionEndTime_ShouldUpdateEndTimeAndStatus() {
        // Arrange
        Session session = new Session();
        session.setId(1L);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Session updated = sessionService.updateSessionEndTime(1L);

        // Assert
        assertNotNull(updated.getEndTime());
        assertEquals("END", updated.getStatus());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void updateSessionStatus_ShouldUpdateStatusAndEndTime_WhenFinished() {
        // Arrange
        Session session = new Session();
        session.setId(1L);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Session updated = sessionService.updateSessionStatus(1L, "FINISHED");

        // Assert
        assertEquals("FINISHED", updated.getStatus());
        assertNotNull(updated.getEndTime());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void deleteSession_ShouldDeleteExistingSession() {
        // Arrange
        when(sessionRepository.existsById(1L)).thenReturn(true);

        // Act
        sessionService.deleteSession(1L);

        // Assert
        verify(sessionRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteSession_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(sessionRepository.existsById(1L)).thenReturn(false);

        // Act & Assert
        assertThrows(SessionNotFoundException.class, () -> sessionService.deleteSession(1L));
        verify(sessionRepository, never()).deleteById(any());
    }

    @Test
    void getSessionById_ShouldReturnSession_WhenExists() {
        // Arrange
        Session session = new Session();
        session.setId(1L);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));

        // Act
        Session result = sessionService.getSessionById(1L);

        // Assert
        assertNotNull(result);
        assertEquals(1L, result.getId());
    }

    @Test
    void getSessionById_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SessionNotFoundException.class, () -> sessionService.getSessionById(1L));
    }

    @Test
    void updateSession_ShouldUpdateAllFields() {
        // Arrange
        User user = new User();
        user.setId(1);

        Session existing = new Session();
        existing.setId(1L);

        Session updates = new Session();
        updates.setUser(user);
        updates.setStartTime(LocalDateTime.now());
        updates.setEndTime(LocalDateTime.now().plusHours(1));
        updates.setStatus("UPDATED");

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsById(1)).thenReturn(true);
        when(sessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Session result = sessionService.updateSession(1L, updates);

        // Assert
        assertEquals(user, result.getUser());
        assertEquals("UPDATED", result.getStatus());
        verify(sessionRepository, times(1)).save(existing);
    }

    @Test
    void getAllSessions_ShouldReturnAllSessions() {
        // Arrange
        Session session1 = new Session();
        Session session2 = new Session();

        when(sessionRepository.findAll()).thenReturn(List.of(session1, session2));

        // Act
        List<Session> result = sessionService.getAllSessions();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void getActiveSessionByUserId_ShouldReturnSession_WhenExists() {
        // Arrange
        Session session = new Session();
        session.setId(1L);

        when(sessionRepository.findActiveSessionByUserId(1L)).thenReturn(Optional.of(session));

        // Act
        Optional<Session> result = sessionService.getActiveSessionByUserId(1L);

        // Assert
        assertTrue(result.isPresent());
        assertEquals(1L, result.get().getId());
    }

    @Test
    void getActiveSessionByUserId_ShouldReturnEmpty_WhenNotExists() {
        // Arrange
        when(sessionRepository.findActiveSessionByUserId(1L)).thenReturn(Optional.empty());

        // Act
        Optional<Session> result = sessionService.getActiveSessionByUserId(1L);

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void getSessionsByUserName_ShouldReturnSessions() {
        // Arrange
        Session session1 = new Session();
        Session session2 = new Session();

        when(sessionRepository.findAllByUserName("testUser")).thenReturn(List.of(session1, session2));

        // Act
        List<Session> result = sessionService.getSessionsByUserName("testUser");

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void getSessionsByUserName_ShouldReturnEmptyList_WhenNoneExist() {
        // Arrange
        when(sessionRepository.findAllByUserName("unknownUser")).thenReturn(List.of());

        // Act
        List<Session> result = sessionService.getSessionsByUserName("unknownUser");

        // Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void validateUserExists_ShouldNotThrow_WhenUserExists() {
        // Arrange
        User user = new User();
        user.setId(1);

        when(userRepository.existsById(1)).thenReturn(true);

        // Act & Assert
        assertDoesNotThrow(() -> sessionService.validateUserExists(user));
    }

    @Test
    void validateUserExists_ShouldThrow_WhenUserDoesNotExist() {
        // Arrange
        User user = new User();
        user.setId(1);

        when(userRepository.existsById(1)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> sessionService.validateUserExists(user));
    }

    @Test
    void validateUserExists_ShouldNotThrow_WhenUserIsNull() {
        // Act & Assert
        assertDoesNotThrow(() -> sessionService.validateUserExists(null));
    }

    @Test
    void getConsoleRepository_ShouldReturnConsoleRepository() {
        // Arrange
        // No need for additional setup since we're just testing the getter

        // Act
        ConsoleRepository result = sessionService.getConsoleRepository();

        // Assert
        assertNotNull(result);
        assertEquals(consoleRepository, result); // Verify it returns the mocked instance
    }

    @Test
    void createSession_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> sessionService.createSession("ACTIVE", 999L));
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void updateSession_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        User invalidUser = new User();
        invalidUser.setId(999);

        Session updates = new Session();
        updates.setUser(invalidUser);

        Session existing = new Session();
        existing.setId(1L);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.existsById(999)).thenReturn(false);

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> sessionService.updateSession(1L, updates));
        verify(sessionRepository, never()).save(any());
    }

    @Test
    void updateSessionStatus_ShouldUpdateStatusWithoutEndTime_WhenNotFinishedOrError() {
        // Arrange
        Session session = new Session();
        session.setId(1L);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Session updated = sessionService.updateSessionStatus(1L, "PAUSED");

        // Assert
        assertEquals("PAUSED", updated.getStatus());
        assertNull(updated.getEndTime());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void updateSessionStatus_ShouldUpdateStatusWithEndTime_WhenError() {
        // Arrange
        Session session = new Session();
        session.setId(1L);

        when(sessionRepository.findById(1L)).thenReturn(Optional.of(session));
        when(sessionRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Session updated = sessionService.updateSessionStatus(1L, "ERROR");

        // Assert
        assertEquals("ERROR", updated.getStatus());
        assertNotNull(updated.getEndTime());
        verify(sessionRepository, times(1)).save(session);
    }

    @Test
    void updateSession_ShouldThrowException_WhenSessionNotFound() {
        // Arrange
        when(sessionRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(SessionNotFoundException.class,
                () -> sessionService.updateSession(1L, new Session()));
        verify(sessionRepository, never()).save(any());
    }
}
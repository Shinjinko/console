package com.console.app.main.service;

import com.console.app.main.exceptions.SessionNotFoundException;
import com.console.app.main.model.Session;
import com.console.app.main.model.User;
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
}
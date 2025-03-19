package com.console.app.main.service;

import com.console.app.main.exceptions.SessionNotFoundException;
import com.console.app.main.model.Session;
import com.console.app.main.model.User;
import com.console.app.main.repository.ConsoleRepository;
import com.console.app.main.repository.SessionRepository;
import com.console.app.main.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;
    private final UserRepository userRepository;
    private final ConsoleRepository consoleRepository;

    public SessionService(SessionRepository sessionRepository, UserRepository userRepository,
                          ConsoleRepository consoleRepository) {
        this.sessionRepository = sessionRepository;
        this.userRepository = userRepository;
        this.consoleRepository = consoleRepository;
    }

    public Optional<Session> getActiveSessionByUserId(Long userId) {
        return sessionRepository.findActiveSessionByUserId(userId);
    }

    @Transactional(readOnly = true)
    public List<Session> getAllSessions() {
        return sessionRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Session getSessionById(Long id) {
        return sessionRepository.findById(id)
                .orElseThrow(() ->
                        new SessionNotFoundException("Session not found with id: " + id));
    }

    @Transactional
    public Session createSession(String status, Long userId) {
        // Проверяем существование пользователя
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() ->
                        new IllegalArgumentException("User not found with id: " + userId));

        // Создаём сессию
        Session session = new Session();
        session.setStartTime(LocalDateTime.now()); // Устанавливаем текущее время
        session.setStatus(status);
        session.setUser(user); // Привязываем пользователя

        return sessionRepository.save(session);
    }



    public Session updateSessionEndTime(Long sessionId) {
        Session session = sessionRepository.findById(sessionId)
                .orElseThrow(() -> new RuntimeException("Session not found"));

        session.setEndTime(LocalDateTime.now());
        session.setStatus("END");
        return sessionRepository.save(session);
    }

    @Transactional
    public Session updateSession(Long id, Session updatedSession) {
        return sessionRepository.findById(id)
                .map(existing -> {
                    validateUserExists(updatedSession.getUser());

                    existing.setUser(updatedSession.getUser());
                    existing.setStartTime(updatedSession.getStartTime());
                    existing.setEndTime(updatedSession.getEndTime());
                    existing.setStatus(updatedSession.getStatus());

                    return sessionRepository.save(existing);
                })
                .orElseThrow(() ->
                        new SessionNotFoundException("Session not found with id: " + id));
    }

    @Transactional
    public Session updateSessionStatus(Long id, String newStatus) {
        return sessionRepository.findById(id)
                .map(session -> {
                    session.setStatus(newStatus);
                    if (newStatus.equals("FINISHED") || newStatus.equals("ERROR")) {
                        session.setEndTime(LocalDateTime.now());
                    }
                    return sessionRepository.save(session);
                })
                .orElseThrow(() ->
                        new SessionNotFoundException("Session not found with id: " + id));
    }

    @Transactional
    public void deleteSession(Long id) {
        if (!sessionRepository.existsById(id)) {
            throw new SessionNotFoundException("Session not found with id: " + id);
        }
        sessionRepository.deleteById(id);
    }

    private void validateUserExists(User user) {
        if (user != null && !userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User not found with id: " + user.getId());
        }
    }

    public ConsoleRepository getConsoleRepository() {
        return consoleRepository;
    }
}
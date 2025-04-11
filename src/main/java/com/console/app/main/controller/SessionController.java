package com.console.app.main.controller;

import com.console.app.main.model.Session;
import com.console.app.main.service.SessionService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/sessions")
public class SessionController {

    private final SessionService sessionService;

    public SessionController(SessionService sessionService) {
        this.sessionService = sessionService;
    }

    @Operation(summary = "Get all sessions", description = "Retrieves a list of all sessions")
    @GetMapping(produces = "application/json")
    public List<Session> getAllSessions() {
        return sessionService.getAllSessions();
    }

    @Operation(summary = "Get session by ID",
            description = "Retrieves a specific session by its ID")
    @GetMapping("/{id}")
    public Session getSessionById(@PathVariable Long id) {
        return sessionService.getSessionById(id);
    }

    @Operation(summary = "Create session",
            description = "Creates a new session with the specified status and user ID")
    @PostMapping("/create")
    public Session createSession(
            @RequestParam String status,
            @RequestParam Long userId) {
        return sessionService.createSession(status, userId);
    }

    @Operation(summary = "Filter sessions by user name",
            description = "Retrieves sessions filtered by user name")
    @GetMapping("/filter/name/{name}")
    public List<Session> filterSessionsByUserName(@PathVariable String name) {
        return sessionService.getSessionsByUserName(name);
    }

    @Operation(summary = "Update session end time",
            description = "Updates the end time of a session to the current time")
    @PatchMapping("/{sessionId}/end")
    public Session updateSessionEndTime(@PathVariable Long sessionId) {
        return sessionService.updateSessionEndTime(sessionId);
    }

    @Operation(summary = "Update session", description = "Updates all fields of a session")
    @PutMapping("/{id}")
    public Session updateSession(@PathVariable Long id, @RequestBody Session session) {
        return sessionService.updateSession(id, session);
    }

    @Operation(summary = "Update session status",
            description = "Updates only the status field of a session")
    @PatchMapping("/{id}/status")
    public Session updateSessionStatus(@PathVariable Long id, @RequestBody String status) {
        return sessionService.updateSessionStatus(id, status);
    }

    @Operation(summary = "Delete session", description = "Deletes a session by its ID")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteSession(@PathVariable Long id) {
        sessionService.deleteSession(id);
    }
}
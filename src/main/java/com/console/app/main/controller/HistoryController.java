package com.console.app.main.controller;

import com.console.app.main.model.HistoryItem;
import com.console.app.main.service.HistoryService;
import com.console.app.main.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/history")
public class HistoryController {

    private final HistoryService historyService;
    private final UserService userService;

    public HistoryController(HistoryService historyService, UserService userService) {
        this.userService = userService;
        this.historyService = historyService;
    }

    @Operation(summary = "Get history for current user", description = "Retrieves the list of history items for the currently authenticated user")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "History retrieved successfully"),
            @ApiResponse(responseCode = "401", description = "Unauthorized"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<HistoryItem> getHistory() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long userId = userService.getUserIdByEmail(email);
        return historyService.getHistoryByUserId(userId);
    }

    @Operation(summary = "Log an action", description = "Logs an action for a user in the history")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Action logged successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping
    public ResponseEntity<String> logAction(@RequestBody HistoryRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();
        Long userId = userService.getUserIdByEmail(email);
        historyService.logAction(userId, request.getDescription());
        return ResponseEntity.ok("Action logged successfully");
    }
}

class HistoryRequest {
    private Long userId;
    private String description;

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
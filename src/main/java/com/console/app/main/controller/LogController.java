package com.console.app.main.controller;

import com.console.app.main.model.LogTask;
import com.console.app.main.service.LogTaskService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.UUID;

@RestController
@RequestMapping("/api/logs")
public class LogController {

    private final LogTaskService logTaskService;

    public LogController(LogTaskService logTaskService) {
        this.logTaskService = logTaskService;
    }

    @Operation(summary = "Create log file asynchronously",
            description = "Initiates log file creation for a specific date and returns task ID")
    @PostMapping("/create")
    public ResponseEntity<UUID> createLogFileAsync(
            @RequestParam @NotNull(message = "Date is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {
        LogTask task = logTaskService.createLogTask(date);
        logTaskService.createLogFileAsync(task.getId(), date);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(task.getId());
    }

    @Operation(summary = "Get log task status",
            description = "Retrieves the status of a log creation task by its ID")
    @GetMapping("/status/{taskId}")
    public ResponseEntity<LogTask> getLogTaskStatus(@PathVariable UUID taskId) {
        LogTask task = logTaskService.getTaskStatus(taskId);
        return ResponseEntity.ok(task);
    }

    @Operation(summary = "Get log file",
            description = "Downloads the log file for a completed task by its ID")
    @GetMapping("/file/{taskId}")
    public ResponseEntity<Resource> getLogFile(@PathVariable UUID taskId) throws IOException {
        try {
            Path filePath = logTaskService.getLogFilePath(taskId);
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists()) {
                throw new IllegalStateException("Log file not found for task: " + taskId);
            }
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .header(HttpHeaders.CONTENT_DISPOSITION,
                            "attachment; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } catch (IllegalStateException | IllegalArgumentException e) {
            throw new RuntimeException("Failed to retrieve log file: " + e.getMessage());
        }
    }
}
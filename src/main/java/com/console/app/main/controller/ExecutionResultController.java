package com.console.app.main.controller;

import com.console.app.main.model.ExecutionResult;
import com.console.app.main.service.ExecutionResultService;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/executions")
public class ExecutionResultController {

    private final ExecutionResultService executionResultService;

    public ExecutionResultController(ExecutionResultService executionResultService) {
        this.executionResultService = executionResultService;
    }

    @Transactional
    @GetMapping(produces = "application/json")
    public List<ExecutionResult> getAllExecutions() {
        return executionResultService.getAllExecutions();
    }

    @GetMapping("/{id}")
    public ExecutionResult getExecutionById(@PathVariable Long id) {
        return executionResultService.getExecutionById(id);
    }

    @PatchMapping("/{id}/time")
    public ExecutionResult updateExecutionTime(
            @PathVariable Long id,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime newTime) {
        return executionResultService.updateExecutionTime(id, newTime);
    }

    @PostMapping("/create")
    public ExecutionResult executeExecution(
            @RequestParam String language,
            @RequestParam String code,
            @RequestParam String result,
            @RequestParam Long consoleId) { // Используем consoleId вместо sessionId
        return executionResultService.createExecution(language, code, result, consoleId);
    }

    @PutMapping("/{id}")
    public ExecutionResult updateExecution(@PathVariable Long id,
                                           @RequestBody ExecutionResult executionResult) {
        return executionResultService.updateExecution(id, executionResult);
    }

    @DeleteMapping("/{id}")
    public void deleteExecution(@PathVariable Long id) {
        executionResultService.deleteExecution(id);
    }
}


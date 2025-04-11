package com.console.app.main.controller;

import com.console.app.main.model.ExecutionResult;
import com.console.app.main.service.ExecutionResultService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @Operation(summary = "Get all executions",
            description = "Retrieves a list of all execution results")
    @Transactional
    @GetMapping(produces = "application/json")
    public List<ExecutionResult> getAllExecutions() {
        return executionResultService.getAllExecutions();
    }

    @Operation(summary = "Get execution by ID",
            description = "Retrieves a specific execution result by its ID")
    @GetMapping("/{id}")
    public ExecutionResult getExecutionById(@PathVariable Long id) {
        return executionResultService.getExecutionById(id);
    }

    @Operation(summary = "Update execution time",
            description = "Updates the timestamp of a specific execution")
    @PatchMapping("/{id}/time")
    public ExecutionResult updateExecutionTime(
            @PathVariable @NotNull(message = "ID is required") Long id,
            @RequestParam @NotNull(message = "New time is required")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime newTime) {
        return executionResultService.updateExecutionTime(id, newTime);
    }

    @Operation(summary = "Create execution",
            description = "Creates a new execution result with the specified parameters")
    @PostMapping("/create")
    public ExecutionResult executeExecution(
            @RequestParam @NotBlank(message = "Language is required") String language,
            @RequestParam @NotBlank(message = "Code is required") String code,
            @RequestParam @NotBlank(message = "Result is required") String result,
            @RequestParam @NotNull(message = "Console ID is required") Long consoleId) {
        return executionResultService.createExecution(language, code, result, consoleId);
    }

    @Operation(summary = "Update execution",
            description = "Updates all fields of an execution result")
    @PutMapping("/{id}")
    public ExecutionResult updateExecution(@PathVariable Long id,
                                           @RequestBody ExecutionResult executionResult) {
        return executionResultService.updateExecution(id, executionResult);
    }

    @Operation(summary = "Delete execution", description = "Deletes an execution result by its ID")
    @DeleteMapping("/{id}")
    public void deleteExecution(@PathVariable Long id) {
        executionResultService.deleteExecution(id);
    }
}


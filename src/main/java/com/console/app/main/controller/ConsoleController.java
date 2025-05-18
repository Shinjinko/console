package com.console.app.main.controller;

import com.console.app.main.model.Console;
import com.console.app.main.repository.UserRepository;
import com.console.app.main.service.ConsoleService;
import com.console.app.main.service.HistoryService;
import com.console.app.main.service.UserService;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consoles")
@Validated
public class ConsoleController {

    private final ConsoleService consoleService;
    private final UserRepository userRepository;
    private final UserService userService;
    private final HistoryController historyController;

    public ConsoleController(ConsoleService consoleService, UserRepository userRepository,
                             UserService userService, HistoryController historyController) {
        this.consoleService = consoleService;
        this.userRepository = userRepository;
        this.userService = userService;
        this.historyController = historyController;
    }

    @Operation(summary = "Get all consoles",
            description = "Retrieves a list of all available consoles")
    @GetMapping(produces = "application/json")
    public List<Console> getAllConsoles() {
        return consoleService.getAllConsoles();
    }

    @Operation(summary = "Create a new console",
            description = "Creates a new console with the specified name and type")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Console createConsole(
            @RequestParam @NotBlank(message = "Name is required") String name,
            @RequestParam @NotBlank(message = "Type is required") String type) {
        Console console = new Console();
        console.setName(name);
        console.setType(type);
        return consoleService.createConsole(console);
    }

    @Operation(summary = "Bulk create consoles",
            description = "Creates multiple consoles from a list")
    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    @JsonIgnoreProperties({"users", "executionResults"})
    public List<Console> createConsolesBulk(@RequestBody List<Console> consoles) {
        return consoles.stream()
                .filter(c -> c.getName() != null && !c.getName().isEmpty())
                .filter(c -> c.getType() != null && !c.getType().isEmpty())
                .map(c -> {
                    Console newConsole = new Console();
                    newConsole.setName(c.getName());
                    newConsole.setType(c.getType());
                    return consoleService.createConsole(newConsole);
                })
                .toList();
    }

    @Operation(summary = "Delete a console", description = "Deletes a console by its ID")
    @DeleteMapping
    public void deleteConsole(@RequestParam @NotNull(message = "ID is required") Long id) {
        consoleService.deleteConsole(id);
    }

    @Operation(summary = "Update a console", description = "Updates the name and type of a console")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Console updated successfully"),
            @ApiResponse(responseCode = "404", description = "Console not found"),
            @ApiResponse(responseCode = "400", description = "Invalid input data"),
            @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{consoleId}")
    @Transactional
    public ResponseEntity<String> updateConsole(
            @PathVariable Long consoleId,
            @RequestBody ConsoleUpdateRequest request) {
        try {
            Console console = consoleService.getconsolebyiD(consoleId)
                    .orElseThrow(() -> new RuntimeException("Console not found"));
            console.setName(request.getName());
            console.setType(request.getType());
            consoleService.save(console);

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            String email = authentication.getName();
            Long userId = userService.getUserIdByEmail(email);
            HistoryRequest historyRequest = new HistoryRequest();
            historyRequest.setUserId(userId);
            historyRequest.setDescription("Обновил консоль ID " + consoleId + " (Имя: " + request.getName() + ", Тип: " + request.getType() + ")");
            historyController.logAction(historyRequest);

            return ResponseEntity.ok("Console updated successfully");
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Invalid input data");
        }
    }
}

class ConsoleUpdateRequest {
    @NotBlank(message = "Name cannot be blank")
    private String name;
    @NotBlank(message = "Type cannot be blank")
    private String type;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
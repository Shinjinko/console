package com.console.app.main.controller;

import com.console.app.main.model.Console;
import com.console.app.main.service.ConsoleService;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consoles")
@Validated
public class ConsoleController {

    private final ConsoleService consoleService;

    public ConsoleController(ConsoleService consoleService) {
        this.consoleService = consoleService;
    }

    @Operation(summary = "Get all consoles", description = "Retrieves a list of all available consoles")
    @GetMapping(produces = "application/json")
    public List<Console> getAllConsoles() {
        return consoleService.getAllConsoles();
    }

    @Operation(summary = "Create a new console", description = "Creates a new console with the specified name and type")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Console createConsole(
            @RequestParam String name,
            @RequestParam String type
    ) {
        Console console = new Console();
        console.setName(name);
        console.setType(type);
        return consoleService.createConsole(console);
    }

    @Operation(summary = "Delete a console", description = "Deletes a console by its ID")
    @DeleteMapping
    public void deleteConsole(@RequestParam Long id) {
        consoleService.deleteConsole(id);
    }
}
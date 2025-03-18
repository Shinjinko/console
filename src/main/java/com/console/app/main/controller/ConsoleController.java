package com.console.app.main.controller;

import com.console.app.main.model.Console;
import com.console.app.main.model.ExecutionResult;
import com.console.app.main.service.ConsoleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/consoles")
public class ConsoleController {

    private final ConsoleService consoleService;

    public ConsoleController(ConsoleService consoleService) {
        this.consoleService = consoleService;
    }

    @GetMapping("/execute")
    public ExecutionResult executeCode(
            @RequestParam String language,
            @RequestParam String code,
            @RequestParam int id) {
        return consoleService.executeCode(language, code, (long) id);
    }

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

    // DELETE с query-параметром id
    @DeleteMapping
    public void deleteConsole(@RequestParam Long id) {
        consoleService.deleteConsole(id);
    }
}
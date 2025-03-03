package com.console.app.main.controller;

import com.console.app.main.model.ExecutionResult;
import com.console.app.main.service.CodeExecutions;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ConsoleController {

    private final CodeExecutions codeExecutions;

    public ConsoleController(CodeExecutions codeExecutions) {
        this.codeExecutions = codeExecutions;
    }

    @GetMapping("/execute")
    public ExecutionResult executeCode(@RequestParam(name = "language") String language,
                                       @RequestParam(name = "code") String code) {
        return codeExecutions.executeCode(language, code);
    }

    @GetMapping("/execute/{language}")
    public ExecutionResult executeCodeByLanguage(@PathVariable String language,
                                                 @RequestParam(name = "code") String code) {
        return codeExecutions.executeCode(language, code);
    }
}
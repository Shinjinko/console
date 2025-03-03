package com.console.app.main.service;

import com.console.app.main.model.ExecutionResult;
import org.springframework.stereotype.Service;

@Service
public class CodeExecutions {
    public ExecutionResult executeCode(String language, String code) {
        return new ExecutionResult(language, code, "Executed successfully");
    }
}
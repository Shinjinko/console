package com.console.app.main.service;

import com.console.app.main.exceptions.ExecutionNotFoundException;
import com.console.app.main.model.Console;
import com.console.app.main.model.ExecutionResult;
import com.console.app.main.repository.ExecutionResultRepository;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ExecutionResultService {

    private final ExecutionResultRepository executionResultRepository;
    private final ConsoleService consoleService;

    // Конструктор с двумя зависимостями
    public ExecutionResultService(ExecutionResultRepository executionResultRepository,
                                  ConsoleService consoleService) {
        this.executionResultRepository = executionResultRepository;
        this.consoleService = consoleService; // Инициализируем consoleService
    }

    // Получение всех результатов выполнения
    public List<ExecutionResult> getAllExecutions() {
        return executionResultRepository.findAll();
    }

    // Получение результата выполнения по ID
    public ExecutionResult getExecutionById(Long id) {
        return executionResultRepository.findById(id)
                .orElseThrow(() ->
                        new ExecutionNotFoundException("Execution not found with id: " + id));
    }

    // Создание нового результата выполнения
    public ExecutionResult createExecution(String language, String code, String result,
                                           Long consoleId) {
        ExecutionResult executionResult = new ExecutionResult();
        Console console = consoleService.getConsoleById(consoleId); // Используем consoleService
        executionResult.setLanguage(language);
        executionResult.setCode(code);
        executionResult.setResult(result);
        executionResult.setConsole(console); // Устанавливаем связь с Console
        executionResult.setTime(LocalDateTime.now()); // Устанавливаем текущее время
        return executionResultRepository.save(executionResult);
    }

    // Полное обновление результата выполнения
    public ExecutionResult updateExecution(Long id, ExecutionResult executionResult) {
        return executionResultRepository.findById(id)
                .map(existing -> {
                    existing.setLanguage(executionResult.getLanguage());
                    existing.setCode(executionResult.getCode());
                    existing.setResult(executionResult.getResult());

                    if (executionResult.getConsole() != null) {
                        existing.setConsole(executionResult.getConsole());
                    }

                    if (executionResult.getTime() != null) {
                        existing.setTime(executionResult.getTime());
                    }

                    return executionResultRepository.save(existing);
                })
                .orElseThrow(() ->
                        new ExecutionNotFoundException("Execution not found with id: " + id));
    }

    // Частичное обновление результата выполнения
    public ExecutionResult patchExecution(Long id, ExecutionResult partialUpdate) {
        return executionResultRepository.findById(id)
                .map(existing -> {

                    if (partialUpdate.getLanguage() != null) {
                        existing.setLanguage(partialUpdate.getLanguage());
                    }

                    if (partialUpdate.getCode() != null) {
                        existing.setCode(partialUpdate.getCode());
                    }

                    if (partialUpdate.getResult() != null) {
                        existing.setResult(partialUpdate.getResult());
                    }

                    if (partialUpdate.getConsole() != null) {
                        existing.setConsole(partialUpdate.getConsole());
                    }

                    if (partialUpdate.getTime() != null) {
                        existing.setTime(partialUpdate.getTime());
                    }

                    return executionResultRepository.save(existing);
                })
                .orElseThrow(() ->
                        new ExecutionNotFoundException("Execution not found with id: " + id));
    }

    // Обновление только времени выполнения
    public ExecutionResult updateExecutionTime(Long id) {
        return executionResultRepository.findById(id)
                .map(existing -> {
                    existing.setTime(LocalDateTime.now());
                    return executionResultRepository.save(existing);
                })
                .orElseThrow(() ->
                        new ExecutionNotFoundException("Execution not found with id: " + id));
    }

    // Удаление результата выполнения
    public void deleteExecution(Long id) {
        if (!executionResultRepository.existsById(id)) {
            throw new ExecutionNotFoundException("Execution not found with id: " + id);
        }
        executionResultRepository.deleteById(id);
    }
}
package com.console.app.main.service;

import com.console.app.main.exceptions.ExecutionNotFoundException;
import com.console.app.main.model.ExecutionResult;
import com.console.app.main.model.Session;
import com.console.app.main.repository.ExecutionResultRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ExecutionResultService {

    private final ExecutionResultRepository executionResultRepository;

    public ExecutionResultService(ExecutionResultRepository executionResultRepository) {
        this.executionResultRepository = executionResultRepository;
    }

    public List<ExecutionResult> getAllExecutions() {
        return executionResultRepository.findAll();
    }

    public ExecutionResult getExecutionById(Long id) {
        return executionResultRepository.findById(id)
                .orElseThrow(() ->
                        new ExecutionNotFoundException("Execution not found with id: " + id));
    }

    public ExecutionResult createExecution(String language, String code, String result,
                                           Long sessionId) {
        ExecutionResult executionResult = new ExecutionResult();
        Session session = new Session();
        session.setId(sessionId);
        executionResult.setLanguage(language);
        executionResult.setCode(code);
        executionResult.setResult(result);
        executionResult.setSession(session);
        return executionResultRepository.save(executionResult);
    }

    public ExecutionResult updateExecution(Long id, ExecutionResult executionResult) {
        return executionResultRepository.findById(id)
                .map(existing -> {
                    existing.setLanguage(executionResult.getLanguage());
                    existing.setCode(executionResult.getCode());
                    existing.setResult(executionResult.getResult());

                    if (executionResult.getSession() != null) {
                        existing.setSession(executionResult.getSession());
                    }
                    return executionResultRepository.save(existing);
                })
                .orElseThrow(() ->
                        new ExecutionNotFoundException("Execution not found with id: " + id));
    }

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

                    if (partialUpdate.getSession() != null) {
                        existing.setSession(partialUpdate.getSession());
                    }
                    return executionResultRepository.save(existing);
                })
                .orElseThrow(() ->
                        new ExecutionNotFoundException("Execution not found with id: " + id));
    }

    public void deleteExecution(Long id) {
        if (!executionResultRepository.existsById(id)) {
            throw new ExecutionNotFoundException("Execution not found with id: " + id);
        }
        executionResultRepository.deleteById(id);
    }
}
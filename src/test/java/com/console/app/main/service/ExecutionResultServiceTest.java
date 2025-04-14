package com.console.app.main.service;

import com.console.app.main.cache.ExecutionResultCache;
import com.console.app.main.exceptions.ExecutionNotFoundException;
import com.console.app.main.model.Console;
import com.console.app.main.model.ExecutionResult;
import com.console.app.main.repository.ExecutionResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExecutionResultServiceTest {

    @Mock
    private ExecutionResultRepository repository;

    @Mock
    private ConsoleService consoleService;

    @Mock
    private ExecutionResultCache cache;

    @InjectMocks
    private ExecutionResultService executionResultService;

    @Test
    void getAllExecutions_ShouldReturnAllExecutionsAndCacheThem() {
        // Arrange
        ExecutionResult result1 = new ExecutionResult("java", "code1", "success", new Console());
        ExecutionResult result2 = new ExecutionResult("python", "code2", "error", new Console());

        when(repository.findAll()).thenReturn(Arrays.asList(result1, result2));

        // Act
        List<ExecutionResult> results = executionResultService.getAllExecutions();

        // Assert
        assertEquals(2, results.size());
        verify(cache, times(1)).put(result1);
        verify(cache, times(1)).put(result2);
        verify(repository, times(1)).findAll();
    }

    @Test
    void getExecutionById_ShouldReturnFromCache_WhenExists() {
        // Arrange
        ExecutionResult result = new ExecutionResult("java", "code", "success", new Console());
        result.setExecutionId(1L);

        when(cache.get(1L)).thenReturn(Optional.of(result));

        // Act
        ExecutionResult found = executionResultService.getExecutionById(1L);

        // Assert
        assertNotNull(found);
        assertEquals("java", found.getLanguage());
        verify(cache, times(1)).get(1L);
        verify(repository, never()).findById(any());
    }

    @Test
    void getExecutionById_ShouldReturnFromRepositoryAndCache_WhenNotInCache() {
        // Arrange
        ExecutionResult result = new ExecutionResult("java", "code", "success", new Console());
        result.setExecutionId(1L);

        when(cache.get(1L)).thenReturn(Optional.empty());
        when(repository.findById(1L)).thenReturn(Optional.of(result));

        // Act
        ExecutionResult found = executionResultService.getExecutionById(1L);

        // Assert
        assertNotNull(found);
        assertEquals("java", found.getLanguage());
        verify(cache, times(1)).get(1L);
        verify(repository, times(1)).findById(1L);
        verify(cache, times(1)).put(result);
    }

    @Test
    void createExecution_ShouldSaveAndCacheResult() {
        // Arrange
        Console console = new Console();
        console.setId(1L);

        when(consoleService.getConsoleById(1L)).thenReturn(console);
        when(repository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        ExecutionResult result = executionResultService.createExecution("java", "code", "success", 1L);

        // Assert
        assertNotNull(result);
        assertEquals("java", result.getLanguage());
        assertEquals(console, result.getConsole());
        verify(repository, times(1)).save(any());
        verify(cache, times(1)).put(any());
    }

    @Test
    void deleteExecution_ShouldRemoveFromCacheAndRepository() {
        // Arrange
        ExecutionResult result = new ExecutionResult("java", "code", "success", new Console());
        result.setExecutionId(1L);

        when(repository.findById(1L)).thenReturn(Optional.of(result));

        // Act
        executionResultService.deleteExecution(1L);

        // Assert
        verify(cache, times(1)).evictFromCache(1L);
        verify(repository, times(1)).delete(result);
    }
}
package com.console.app.main.service;

import com.console.app.main.model.Console;
import com.console.app.main.model.ExecutionResult;
import com.console.app.main.repository.ConsoleRepository;
import com.console.app.main.repository.ExecutionResultRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsoleServiceTest {

    @Mock
    private ConsoleRepository consoleRepository;

    @Mock
    private ExecutionResultRepository executionResultRepository;

    @InjectMocks
    private ConsoleService consoleService;

    @Test
    void getAllConsoles_ShouldReturnAllConsoles() {
        // Arrange
        Console console1 = new Console();
        console1.setId(1L);
        console1.setName("Console 1");

        Console console2 = new Console();
        console2.setId(2L);
        console2.setName("Console 2");

        when(consoleRepository.findAll()).thenReturn(Arrays.asList(console1, console2));

        // Act
        List<Console> consoles = consoleService.getAllConsoles();

        // Assert
        assertEquals(2, consoles.size());
        assertEquals("Console 1", consoles.getFirst().getName());
        verify(consoleRepository, times(1)).findAll();
    }

    @Test
    void getConsoleById_ShouldReturnConsole_WhenExists() {
        // Arrange
        Console console = new Console();
        console.setId(1L);
        console.setName("Test Console");

        when(consoleRepository.findById(1L)).thenReturn(Optional.of(console));

        // Act
        Console found = consoleService.getConsoleById(1L);

        // Assert
        assertNotNull(found);
        assertEquals("Test Console", found.getName());
        verify(consoleRepository, times(1)).findById(1L);
    }

    @Test
    void getConsoleById_ShouldThrowException_WhenNotExists() {
        // Arrange
        when(consoleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> consoleService.getConsoleById(1L));
        verify(consoleRepository, times(1)).findById(1L);
    }

    @Test
    void createConsole_ShouldSaveConsole() {
        // Arrange
        Console console = new Console();
        console.setName("New Console");

        when(consoleRepository.save(console)).thenReturn(console);

        // Act
        Console created = consoleService.createConsole(console);

        // Assert
        assertNotNull(created);
        assertEquals("New Console", created.getName());
        verify(consoleRepository, times(1)).save(console);
    }

    @Test
    void executeCode_ShouldReturnExecutionResult() {
        // Arrange
        Console console = new Console();
        console.setId(1L);

        when(consoleRepository.findById(1L)).thenReturn(Optional.of(console));

        // Act
        ExecutionResult result = consoleService.executeCode("java", "System.out.println(\"Hello\");", 1L);

        // Assert
        assertNotNull(result);
        assertEquals("java", result.getLanguage());
        assertNotNull(result.getResult());
        assertEquals(console, result.getConsole());
        verify(executionResultRepository, times(1)).save(result);
    }
}
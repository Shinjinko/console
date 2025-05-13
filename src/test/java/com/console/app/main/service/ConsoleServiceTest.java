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

        // Мокируем сохранение результата
        ExecutionResult mockResult = new ExecutionResult("java", "System.out.println(\"Hello\");",
                "Выполнено успешно", console);
        when(executionResultRepository.save(any(ExecutionResult.class))).thenReturn(mockResult);

        // Act
        ExecutionResult result = consoleService.executeCode("java", "System.out.println(\"Hello\");", 1L);

        // Assert
        assertNotNull(result);
        assertEquals("java", result.getLanguage());
        assertNotNull(result.getResult());
        assertEquals(console, result.getConsole());
        verify(executionResultRepository, times(1)).save(any(ExecutionResult.class));
    }

    @Test
    void createConsolesBulk_ShouldSaveValidConsoles() {
        // Arrange
        Console console1 = new Console();
        console1.setName("Valid 1");
        console1.setType("Type 1");

        Console console2 = new Console();
        console2.setName("Valid 2");
        console2.setType("Type 2");

        Console invalidConsole = new Console(); // Без имени и типа

        when(consoleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<Console> result = consoleService.createConsolesBulk(
                List.of(console1, console2, invalidConsole));

        // Assert
        assertEquals(2, result.size());
        verify(consoleRepository, times(2)).save(any());
    }

    @Test
    void updateConsole_ShouldUpdateExistingConsole() {
        // Arrange
        Console existing = new Console();
        existing.setId(1L);
        existing.setName("Old Name");
        existing.setType("Old Type");

        Console updated = new Console();
        updated.setName("New Name");
        updated.setType("New Type");

        when(consoleRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(consoleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Console result = consoleService.updateConsole(1L, updated);

        // Assert
        assertEquals("New Name", result.getName());
        assertEquals("New Type", result.getType());
        assertEquals(1L, result.getId());
        verify(consoleRepository, times(1)).save(existing);
    }

    @Test
    void createConsolesBulk_ShouldIgnoreInvalidConsoles() {
        // Arrange
        Console validConsole = new Console();
        validConsole.setName("Valid");
        validConsole.setType("Type");

        Console nullNameConsole = new Console();
        nullNameConsole.setType("Type");

        Console emptyNameConsole = new Console();
        emptyNameConsole.setName("");
        emptyNameConsole.setType("Type");

        when(consoleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<Console> result = consoleService.createConsolesBulk(
                List.of(validConsole, nullNameConsole, emptyNameConsole));

        // Assert
        assertEquals(1, result.size());
        assertEquals("Valid", result.getFirst().getName());
        verify(consoleRepository, times(1)).save(any());
    }

    @Test
    void updateConsole_ShouldThrowException_WhenConsoleNotFound() {
        // Arrange
        Long id = 1L;
        Console updated = new Console();
        updated.setName("New Name");

        when(consoleRepository.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () -> consoleService.updateConsole(id, updated));

        verify(consoleRepository, times(1)).findById(id);
        verify(consoleRepository, never()).save(any());
    }


    @Test
    void deleteConsole_ShouldDeleteConsole() {
        // Arrange
        doNothing().when(consoleRepository).deleteById(1L);

        // Act
        consoleService.deleteConsole(1L);

        // Assert
        verify(consoleRepository, times(1)).deleteById(1L);
    }

    @Test
    void createConsolesBulk_ShouldIgnoreInvalidConsoles_AllCases() {
        // Arrange
        Console validConsole = new Console();
        validConsole.setName("Valid");
        validConsole.setType("Type");

        Console nullNameConsole = new Console();
        nullNameConsole.setType("Type");

        Console emptyNameConsole = new Console();
        emptyNameConsole.setName("");
        emptyNameConsole.setType("Type");

        Console nullTypeConsole = new Console();
        nullTypeConsole.setName("Name");

        Console emptyTypeConsole = new Console();
        emptyTypeConsole.setName("Name");
        emptyTypeConsole.setType("");

        when(consoleRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        List<Console> result = consoleService.createConsolesBulk(
                List.of(validConsole, nullNameConsole, emptyNameConsole, nullTypeConsole, emptyTypeConsole));

        // Assert
        assertEquals(1, result.size());
        assertEquals("Valid", result.getFirst().getName());
        verify(consoleRepository, times(1)).save(any());
    }

    @Test
    void executeCode_ShouldReturnAllPossibleStatusMessages() {

        Console console = new Console();
        console.setId(1L);

        when(consoleRepository.findById(1L)).thenReturn(Optional.of(console));
        when(executionResultRepository.save(any(ExecutionResult.class))).thenAnswer(invocation -> invocation.getArgument(0));

        for (int i = 0; i < 100; i++) {
            ExecutionResult result = consoleService.executeCode("java", "System.out.println(\"Hello\");", 1L);
            assertTrue(Arrays.asList(ConsoleService.STATUS_MESSAGES).contains(result.getResult()));
        }

        verify(executionResultRepository, atLeastOnce()).save(any(ExecutionResult.class));
    }


}
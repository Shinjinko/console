package com.console.app.main.service;

import com.console.app.main.exceptions.ValidationException;
import com.console.app.main.model.Console;
import com.console.app.main.model.ExecutionResult;
import com.console.app.main.model.User;
import com.console.app.main.repository.UserRepository;
import com.console.app.main.repository.ConsoleRepository;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Mock
    private ConsoleRepository consoleRepository;

    @Test
    void createUser_ShouldSaveNewUser() {
        // Arrange
        User user = new User();
        user.setName("test");
        user.setEmail("test@example.com");
        user.setPassword("password");

        when(userRepository.save(any())).thenReturn(user);

        // Act
        User created = userService.createUser("test", "test@example.com", "password");

        // Assert
        assertNotNull(created);
        assertEquals("test", created.getName());
        verify(userRepository, times(1)).save(any());
    }

    @Test
    void createUser_ShouldThrowException_WhenNameExists() {
        // Arrange
        when(userRepository.save(any())).thenThrow(
                new DataIntegrityViolationException("users_name_key"));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.createUser("test", "test@example.com", "password"));

        assertEquals("User with this name already exists", exception.getMessage());
    }

    @Test
    void getUserById_ShouldReturnUser_WhenExists() {
        // Arrange
        User user = new User();
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Act
        Optional<User> found = userService.getUserById(1);

        // Assert
        assertTrue(found.isPresent());
        assertEquals(1, found.get().getId());
    }

    @Test
    void deleteUser_ShouldDeleteExistingUser() {
        // Act
        userService.deleteUser(1);

        // Assert
        verify(userRepository, times(1)).deleteById(1);
    }

    @Test
    void getAllUsers_ShouldReturnAllUsers() {
        // Arrange
        User user1 = new User();
        User user2 = new User();

        when(userRepository.findAll()).thenReturn(List.of(user1, user2));

        // Act
        List<User> result = userService.getAllUsers();

        // Assert
        assertEquals(2, result.size());
    }

    @Test
    void getUserByName_ShouldReturnUser_WhenExists() {
        // Arrange
        User user = new User();
        user.setName("test");

        when(userRepository.findByNameIgnoreCase("test")).thenReturn(Optional.of(user));

        // Act
        Optional<User> result = userService.getUserByName("test");

        // Assert
        assertTrue(result.isPresent());
        assertEquals("test", result.get().getName());
    }

    @Test
    void patchUser_ShouldUpdateSpecifiedFields() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setName("old");
        user.setEmail("old@test.com");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<User> result = userService.patchUser(1,
                Map.of("name", "new", "email", "new@test.com"));

        // Assert
        assertTrue(result.isPresent());
        assertEquals("new", result.get().getName());
        assertEquals("new@test.com", result.get().getEmail());
    }

    @Test
    void patchUser_ShouldThrowException_ForInvalidField() {
        // Arrange
        User user = new User();
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                userService.patchUser(1, Map.of("invalid", "value")));
    }

    @Test
    void addConsoleToUser_ShouldAddConsole_WhenUserAndConsoleExist() {
        // Arrange
        User user = new User();
        user.setId(1);
        Console console = new Console();
        console.setId(1L);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(consoleRepository.findById(1L)).thenReturn(Optional.of(console));
        when(userRepository.save(any(User.class))).thenReturn(user);

        // Act
        userService.addConsoleToUser(1L, 1L);

        // Assert
        verify(userRepository, times(1)).save(user);
        assertTrue(user.getConsoles().contains(console));
    }

    @Test
    void addConsoleToUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                userService.addConsoleToUser(1L, 1L));
    }

    @Test
    void addConsoleToUser_ShouldThrowException_WhenConsoleNotFound() {
        // Arrange
        User user = new User();
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(consoleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                userService.addConsoleToUser(1L, 1L));
    }

    @Test
    void removeConsoleFromUser_ShouldRemoveConsole_WhenUserAndConsoleExist() {
        // Arrange
        User user = new User();
        user.setId(1);
        Console console = new Console();
        console.setId(1L);
        user.getConsoles().add(console);
        console.getUsers().add(user);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(consoleRepository.findById(1L)).thenReturn(Optional.of(console));

        // Act
        userService.removeConsoleFromUser(1L, 1L);

        // Assert
        verify(userRepository, times(1)).save(user);
        verify(consoleRepository, times(1)).save(console);
        assertFalse(user.getConsoles().contains(console));
    }

    @Test
    void removeConsoleFromUser_ShouldThrowException_WhenUserNotFound() {
        // Arrange
        when(userRepository.findById(anyInt())).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                userService.removeConsoleFromUser(1L, 1L));
    }

    @Test
    void removeConsoleFromUser_ShouldThrowException_WhenConsoleNotFound() {
        // Arrange
        User user = new User();
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(consoleRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(RuntimeException.class, () ->
                userService.removeConsoleFromUser(1L, 1L));
    }

    @Test
    void getUserExecutions_ShouldReturnNull() {
        // Act
        List<ExecutionResult> results = userService.getUserExecutions(1);

        // Assert
        assertNull(results);
    }

    @Test
    void updateUser_ShouldReturnNull() {
        // Arrange
        User user = new User();
        user.setId(1);

        // Act
        Optional<Object> result = userService.updateUser(1, user);

        // Assert
        assertNull(result);
    }

    @Test
    void createUser_ShouldThrowException_WhenEmailExists() {
        // Arrange
        when(userRepository.save(any())).thenThrow(
                new DataIntegrityViolationException("users_email_key"));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.createUser("test", "test@example.com", "password"));

        assertEquals("User with this email already exists", exception.getMessage());
    }

    @Test
    void createUser_ShouldThrowGenericException_WhenUnknownConstraintViolation() {
        // Arrange
        when(userRepository.save(any())).thenThrow(
                new DataIntegrityViolationException("unknown_constraint"));

        // Act & Assert
        ValidationException exception = assertThrows(ValidationException.class,
                () -> userService.createUser("test", "test@example.com", "password"));

        assertTrue(exception.getMessage().startsWith("Database error:"));
    }

    @Test
    void patchUser_ShouldUpdatePassword() {
        // Arrange
        User user = new User();
        user.setId(1);
        user.setPassword("oldPassword");

        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(userRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        Optional<User> result = userService.patchUser(1,
                Map.of("password", "newPassword"));

        // Assert
        assertTrue(result.isPresent());
        assertEquals("newPassword", result.get().getPassword());
    }

    @Test
    void patchUser_ShouldThrowException_ForUnknownField() {

        User user = new User();
        user.setId(1);

        when(userRepository.findById(1)).thenReturn(Optional.of(user));

        assertThrows(RuntimeException.class, () ->
                userService.patchUser(1, Map.of("invalidField", "value")));
    }

    @Test
    void createUsersBulk_ShouldCreateUsersFromValidMaps() {

        List<Map<String, String>> userMaps = List.of(
                Map.of("name", "user1", "email", "user1@test.com", "password", "pass1"),
                Map.of("name", "user2", "email", "user2@test.com", "password", "pass2")
        );

        User user1 = new User();
        user1.setName("user1");
        user1.setEmail("user1@test.com");
        user1.setPassword("pass1");

        User user2 = new User();
        user2.setName("user2");
        user2.setEmail("user2@test.com");
        user2.setPassword("pass2");

        when(userRepository.save(any(User.class)))
                .thenReturn(user1)
                .thenReturn(user2);

        List<User> result = userService.createUsersBulk(userMaps);

        assertEquals(2, result.size());
        assertEquals("user1", result.get(0).getName());
        assertEquals("user2", result.get(1).getName());
        verify(userRepository, times(2)).save(any(User.class));
    }

    @Test
    void createUsersBulk_ShouldFilterInvalidMaps() {
        // Arrange
        List<Map<String, String>> userMaps = List.of(
                Map.of("name", "user1"),
                Map.of("email", "user2@test.com", "password", "pass2"),
                Map.of("name", "user3", "email", "user3@test.com", "password", "pass3")
        );

        User user3 = new User();
        user3.setName("user3");
        user3.setEmail("user3@test.com");
        user3.setPassword("pass3");

        when(userRepository.save(any(User.class))).thenReturn(user3);

        // Act
        List<User> result = userService.createUsersBulk(userMaps);

        // Assert
        assertEquals(1, result.size());
        assertEquals("user3", result.getFirst().getName());
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void createUsersBulk_ShouldHandleEmptyList() {
        // Arrange
        List<Map<String, String>> userMaps = List.of();

        // Act
        List<User> result = userService.createUsersBulk(userMaps);

        // Assert
        assertTrue(result.isEmpty());
        verify(userRepository, never()).save(any());
    }
}
package com.console.app.main.service;

import com.console.app.main.exceptions.ValidationException;
import com.console.app.main.model.Console;
import com.console.app.main.model.User;
import com.console.app.main.repository.UserRepository;
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
}
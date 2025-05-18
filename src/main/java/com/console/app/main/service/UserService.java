package com.console.app.main.service;

import com.console.app.main.exceptions.ValidationException;
import com.console.app.main.model.Console;
import com.console.app.main.model.ExecutionResult;
import com.console.app.main.model.User;
import com.console.app.main.repository.ConsoleRepository;
import com.console.app.main.repository.UserRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {
    private final UserRepository userRepository;
    private final ConsoleRepository consoleRepository;

    public UserService(UserRepository userRepository, ConsoleRepository consoleRepository) {
        this.userRepository = userRepository;
        this.consoleRepository = consoleRepository;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public Optional<User> getUserById(int id) {
        return userRepository.findById(id);
    }

    public Optional<User> getUserByName(String name) {
        return userRepository.findByNameIgnoreCase(name);
    }

    public User createUser(String name, String email, String password) {
        try {
            User user = new User();
            user.setName(name);
            user.setEmail(email);
            user.setPassword(password);
            return userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            if (e.getMessage().contains("users_name_key")) {
                throw new ValidationException("User with this name already exists", 404);
            } else if (e.getMessage().contains("users_email_key")) {
                throw new ValidationException("User with this email already exists", 404);
            }
            throw new ValidationException("Database error: " + e.getMessage(), 404);
        }
    }

    public List<User> createUsersBulk(List<Map<String, String>> userMaps) {
        return userMaps.stream()
                .filter(map ->
                        map.containsKey("name")
                                && map.containsKey("email") && map.containsKey("password"))
                .map(map -> {
                    User user = new User();
                    user.setName(map.get("name"));
                    user.setEmail(map.get("email"));
                    user.setPassword(map.get("password"));
                    return userRepository.save(user);
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public void addConsoleToUser(Long userId, Long consoleId) {
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("User not found with id: " + userId));

        Console console = consoleRepository.findById(consoleId)
                .orElseThrow(() -> new RuntimeException("Console not found with id: " + consoleId));

        user.getConsoles().add(console);
        userRepository.save(user);

    }

    @Transactional
    public void removeConsoleFromUser(Long userId, Long consoleId) {
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
        Console console = consoleRepository.findById(consoleId)
                .orElseThrow(() -> new RuntimeException("Console not found"));
        user.getConsoles().remove(console);
        console.getUsers().remove(user);
        userRepository.save(user);
        consoleRepository.save(console);
    }

    public Optional<User> patchUser(int id, Map<String, Object> updates) {
        return userRepository.findById(id).map(user -> {
            updates.forEach((key, value) -> {
                switch (key) {
                    case "name" -> user.setName((String) value);
                    case "email" -> user.setEmail((String) value);
                    case "password" -> user.setPassword((String) value); // Убрали хэширование
                    default -> throw new RuntimeException("Invalid key: " + key);
                }
            });
            return userRepository.save(user);
        });
    }

    public Long getUserIdByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));
        return (long) user.getId();
    }

    public void deleteUser(int id) {
        userRepository.deleteById(id);
    }

    public List<ExecutionResult> getUserExecutions(int id) {
        return null;
    }

    public Optional<Object> updateUser(int id, User user) {
        return null;
    }
}
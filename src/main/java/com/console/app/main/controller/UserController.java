package com.console.app.main.controller;

import com.console.app.main.exceptions.UserNotFoundExceptions;
import com.console.app.main.model.ExecutionResult;
import com.console.app.main.model.User;
import com.console.app.main.service.UserService;
import java.util.List;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/create")
    public User createUser(
            @RequestParam String name,
            @RequestParam String email,
            @RequestParam String password) {
        return userService.createUser(name, password, email);
    }

    @PostMapping("/{userId}/consoles/{consoleId}")
    public ResponseEntity<String> addConsoleToUser(@PathVariable Long userId,
                                                   @PathVariable Long consoleId) {
        userService.addConsoleToUser(userId, consoleId);
        return ResponseEntity.ok("Console added to user successfully");
    }


    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundExceptions("User not found with id: " + id));
    }

    @GetMapping("/search")
    public User getUserByName(@RequestParam String name) {
        return userService.getUserByName(name)
                .orElseThrow(() -> new UserNotFoundExceptions("User not found with name: " + name));
    }

    @PutMapping("/{id}")
    public User updateUser(@PathVariable int id, @RequestBody User user) {
        return (User) userService.updateUser(id, user)
                .orElseThrow(() -> new UserNotFoundExceptions("User not found with id: " + id));
    }

    @PatchMapping("/{id}")
    public User patchUser(@PathVariable int id, @RequestBody Map<String, Object> updates) {
        return userService.patchUser(id, updates)
                .orElseThrow(() -> new UserNotFoundExceptions("User not found with id: " + id));
    }

    //TODO сделать какой-нить возврат

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }

    @GetMapping("/{id}/executions")
    public List<ExecutionResult> getUserExecutions(@PathVariable int id) {
        return userService.getUserExecutions(id);
    }
}
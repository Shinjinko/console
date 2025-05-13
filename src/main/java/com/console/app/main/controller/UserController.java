package com.console.app.main.controller;

import com.console.app.main.exceptions.UserNotFoundExceptions;
import com.console.app.main.exceptions.ValidationException;
import com.console.app.main.model.ExecutionResult;
import com.console.app.main.model.User;
import com.console.app.main.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @Operation(summary = "Create new user", description = "Creates a new user account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/create")
    public ResponseEntity<?> createUser(
            @Parameter(description = "User name (must not be blank)", required = true)
            @Valid @RequestParam @NotBlank(message = "Name cannot be blank")
            String name,

            @Parameter(description = "User email (must be valid)", required = true)
            @Valid @RequestParam
            @Email(message = "Email should be valid")
            String email,

            @Parameter(description = "User password (min 6 characters)", required = true)
            @Valid @RequestParam
            @Size(min = 6, message = "Password must be at least 6 characters")
            String password) {

        try {
            User user = userService.createUser(name, password, email);
            return ResponseEntity.status(HttpStatus.CREATED).body(user);
        } catch (ValidationException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Internal server error");
        }

    }

    @Operation(summary = "Add console to user",
            description = "Associates a console with a user by their IDs")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Console added successfully"),
        @ApiResponse(responseCode = "404", description = "User or console not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PostMapping("/{userId}/consoles/{consoleId}")
    public ResponseEntity<String> addConsoleToUser(@PathVariable Long userId,
                                                   @PathVariable Long consoleId) {
        userService.addConsoleToUser(userId, consoleId);
        return ResponseEntity.ok("Console added to user successfully");
    }

    @Operation(summary = "Get all users",
            description = "Retrieves a list of all users in the system")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Successfully retrieved list"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping
    public List<User> getAllUsers() {
        return userService.getAllUsers();
    }

    @Operation(summary = "Bulk create users", description = "Creates multiple users at once")
    @PostMapping("/bulk")
    @ResponseStatus(HttpStatus.CREATED)
    public List<User> createUsersBulk(@RequestBody List<Map<String, String>> userMaps) {
        return userService.createUsersBulk(userMaps);
    }

    @Operation(summary = "Get user by ID",
            description = "Retrieves a single user by their unique ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}")
    public User getUserById(@PathVariable int id) {
        return userService.getUserById(id)
                .orElseThrow(() -> new UserNotFoundExceptions("User not found with id: " + id));
    }

    @Operation(summary = "Search user by name",
            description = "Finds a user by their exact name")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/search")
    public User getUserByName(@RequestParam String name) {
        return userService.getUserByName(name)
                .orElseThrow(() -> new UserNotFoundExceptions("User not found with name: " + name));
    }

    @Operation(summary = "Update user",
            description = "Replaces all user data with the provided values")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PutMapping("/{id}")
    public User updateUser(@PathVariable int id, @RequestBody User user) {
        return (User) userService.updateUser(id, user)
                .orElseThrow(() -> new UserNotFoundExceptions("User not found with id: " + id));
    }

    @Operation(summary = "Partial update user",
            description = "Updates specific fields of a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User patched successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input data"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @PatchMapping("/{id}")
    public User patchUser(@PathVariable int id, @RequestBody Map<String, Object> updates) {
        return userService.patchUser(id, updates)
                .orElseThrow(() -> new UserNotFoundExceptions("User not found with id: " + id));
    }

    @Operation(summary = "Delete user",
            description = "Removes a user from the system by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable int id) {
        userService.deleteUser(id);
    }

    @Operation(summary = "Remove console from user",
            description = "Disassociates a console from a user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Console removed successfully"),
        @ApiResponse(responseCode = "404", description = "User or console not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @DeleteMapping("/users/{userId}/consoles/{consoleId}")
    public ResponseEntity<?> removeConsoleFromUser(@PathVariable Long userId,
                                                   @PathVariable Long consoleId) {
        userService.removeConsoleFromUser(userId, consoleId);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Get user executions",
            description = "Retrieves all execution results for a specific user")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Executions retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    @GetMapping("/{id}/executions")
    public List<ExecutionResult> getUserExecutions(@PathVariable int id) {
        return userService.getUserExecutions(id);
    }
}
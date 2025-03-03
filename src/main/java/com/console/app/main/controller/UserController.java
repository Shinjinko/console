package com.console.app.main.controller;

import com.console.app.main.model.User;
import com.console.app.main.service.UserService;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // GET endpoint с Query Parameters
    @GetMapping(value = "/greeting", produces = "text/plain")
    public String greeting(@RequestParam(name = "name", defaultValue = "World") String name) {
        return "Hello, " + name + "!";
    }

    // GET endpoint с Path Parameters
    @GetMapping("/users/{id}")
    public ResponseEntity<?> getUserById(@PathVariable int id) {
        Optional<User> userOptional = userService.getUserById(id);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(404).body("{\"" + "User not found with id: " + id + "\"}");
        }
    }

    // GET endpoint для поиска пользователя по имени
    @GetMapping("/users/by-name/{name}")
    public ResponseEntity<?> getUserByName(@PathVariable String name) {
        Optional<User> userOptional = userService.getUserByName(name);

        if (userOptional.isPresent()) {
            return ResponseEntity.ok(userOptional.get());
        } else {
            return ResponseEntity.status(404).body("{\"" + "User not found with name: " + name + "\"}");
        }
    }
}
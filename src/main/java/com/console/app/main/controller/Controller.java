package com.console.app.main.controller;

import com.console.app.main.model.ExecutionResult;
import com.console.app.main.model.User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class Controller {

  // GET endpoint с Query Parameters
  @GetMapping("/greeting")
  public String greeting(@RequestParam(name = "name", defaultValue = "World") String name) {
    return "Hello, " + name + "!";
  }

  // GET endpoint с Path Parameters
  @GetMapping("/user/{id}")
  public User getUserById(@PathVariable int id) {
    return new User(id, "John Doe", "john.doe@example.com");
  }

  @GetMapping("/execute")
  public ExecutionResult executeCode(@RequestParam(name = "language") String language,
                                     @RequestParam(name = "code") String code) {
    //add
    return new ExecutionResult(language, code, "Executed successfully");
  }

  @GetMapping("/execute/{language}")
  public ExecutionResult executeCodeByLanguage(@PathVariable String language,
                                               @RequestParam(name = "code") String code) {
    //add
    return new ExecutionResult(language, code, "Executed successfully");
  }
}
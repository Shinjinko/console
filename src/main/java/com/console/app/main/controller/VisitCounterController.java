package com.console.app.main.controller;

import com.console.app.main.service.VisitCounterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/visits")
public class VisitCounterController {

    private final VisitCounterService visitCounterService; // Правильное имя переменной

    public VisitCounterController(VisitCounterService visitCounterService) {
        this.visitCounterService = visitCounterService;
    }

    @PostMapping("/record")
    public ResponseEntity<Long> recordVisit() {
        long count = visitCounterService.increment();
        return ResponseEntity.ok(count);
    }

    @GetMapping("/count")
    public ResponseEntity<Long> getVisitCount() {
        return ResponseEntity.ok(visitCounterService.getCount());
    }

    @GetMapping("/internal/count")
    public ResponseEntity<Long> getExactCount() {
        return ResponseEntity.ok(visitCounterService.getCount());
    }

    @PostMapping("/internal/reset")
    public void resetCounter() {
        visitCounterService.reset();
    }
}
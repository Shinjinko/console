package com.console.app.main.controller;

import com.console.app.main.model.HistoryItem;
import com.console.app.main.service.HistoryService;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/history")
public class HistoryController {
    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public ResponseEntity<List<HistoryItem>> getHistory() {
        return ResponseEntity.ok(historyService.getUserHistory());
    }

    @PostMapping
    public ResponseEntity<HistoryItem> logAction(@RequestBody String description) {
        return ResponseEntity.ok(historyService.logAction(description));
    }
}
package com.console.app.main.controller;

import com.console.app.main.cache.ExecutionResultCache;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/executions/cache")
public class ExecutionResultCacheController {
    private final ExecutionResultCache executionResultCache;

    public ExecutionResultCacheController(ExecutionResultCache executionResultCache) {
        this.executionResultCache = executionResultCache;
    }

    @GetMapping("/contents")
    public ResponseEntity<Map<Long, ExecutionResultCache.CachedExecutionInfo>> getCacheContents() {
        return ResponseEntity.ok(executionResultCache.getCacheContents());
    }

    @GetMapping("/details/{id}")
    public ResponseEntity<ExecutionResultCache.CachedExecutionInfo> getCacheDetails(
            @PathVariable Long id) {
        return Optional.ofNullable(executionResultCache.getCacheContents().get(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/stats")
    public ResponseEntity<ExecutionResultCache.CacheStats> getCacheStats() {
        return ResponseEntity.ok(executionResultCache.getCacheStats());
    }

    @GetMapping("/size")
    public ResponseEntity<Integer> getCacheSize() {
        return ResponseEntity.ok(executionResultCache.getCacheContents().size());
    }

    @PostMapping("/refresh/{id}")
    public ResponseEntity<String> refreshCacheEntry(@PathVariable Long id) {
        executionResultCache.remove(id);
        return ResponseEntity.ok("Cache entry refreshed for ID: " + id);
    }

    @PostMapping("/refresh")
    public ResponseEntity<String> refreshEntireCache() {
        executionResultCache.clear();
        return ResponseEntity.ok("Entire cache refreshed");
    }

    @GetMapping("/search")
    public ResponseEntity<Map<Long, ExecutionResultCache.CachedExecutionInfo>> searchInCache(
            @RequestParam String keyword) {
        Map<Long, ExecutionResultCache.CachedExecutionInfo> results =
                executionResultCache.getCacheContents().entrySet().stream()
                        .filter(entry -> entry.getValue().getResult().contains(keyword)
                                ||
                                entry.getValue().getLanguage().contains(keyword))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return ResponseEntity.ok(results);
    }
}
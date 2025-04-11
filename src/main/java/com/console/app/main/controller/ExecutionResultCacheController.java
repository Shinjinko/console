package com.console.app.main.controller;

import com.console.app.main.cache.ExecutionResultCache;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/executions/cache")
@Validated
public class ExecutionResultCacheController {
    private final ExecutionResultCache executionResultCache;

    public ExecutionResultCacheController(ExecutionResultCache executionResultCache) {
        this.executionResultCache = executionResultCache;
    }

    @Operation(summary = "Get cache contents",
            description = "Retrieves all entries from the execution result cache")
    @GetMapping("/contents")
    public ResponseEntity<Map<Long, ExecutionResultCache.CachedExecutionInfo>> getCacheContents() {
        return ResponseEntity.ok(executionResultCache.getCacheContents());
    }

    @Operation(summary = "Get cache details",
            description = "Retrieves detailed information about a specific cache entry by ID")
    @GetMapping("/details/{id}")
    public ResponseEntity<ExecutionResultCache.CachedExecutionInfo> getCacheDetails(
            @PathVariable @NotNull(message = "ID is required") Long id) {
        return Optional.ofNullable(executionResultCache.getCacheContents().get(id))
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @Operation(summary = "Get cache statistics",
            description = "Retrieves statistics about the cache usage and performance")
    @GetMapping("/stats")
    public ResponseEntity<ExecutionResultCache.CacheStats> getCacheStats() {
        return ResponseEntity.ok(executionResultCache.getCacheStats());
    }

    @Operation(summary = "Get cache size",
            description = "Returns the current number of entries in the cache")
    @GetMapping("/size")
    public ResponseEntity<Integer> getCacheSize() {
        return ResponseEntity.ok(executionResultCache.getCacheContents().size());
    }

    @Operation(summary = "Refresh cache entry",
            description = "Removes and refreshes a specific cache entry by ID")
    @PostMapping("/refresh/{id}")
    public ResponseEntity<String> refreshCacheEntry(
            @PathVariable @NotNull(message = "ID is required") Long id) {
        executionResultCache.remove(id);
        return ResponseEntity.ok("Cache entry refreshed for ID: " + id);
    }

    @Operation(summary = "Refresh entire cache",
            description = "Clears and refreshes all entries in the cache")
    @PostMapping("/refresh")
    public ResponseEntity<String> refreshEntireCache() {
        executionResultCache.clear();
        return ResponseEntity.ok("Entire cache refreshed");
    }

    @Operation(summary = "Search in cache",
            description = "Searches cache entries by keyword in result or language fields")
    @GetMapping("/search")
    public ResponseEntity<Map<Long, ExecutionResultCache.CachedExecutionInfo>> searchInCache(
            @RequestParam @NotBlank(message = "Keyword is required") String keyword) {
        Map<Long, ExecutionResultCache.CachedExecutionInfo> results =
                executionResultCache.getCacheContents().entrySet().stream()
                        .filter(entry -> entry.getValue().getResult().contains(keyword)
                                ||
                                entry.getValue().getLanguage().contains(keyword))
                        .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

        return ResponseEntity.ok(results);
    }
}
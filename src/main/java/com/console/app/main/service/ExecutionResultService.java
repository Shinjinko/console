package com.console.app.main.service;

import com.console.app.main.cache.ExecutionResultCache;
import com.console.app.main.exceptions.ExecutionNotFoundException;
import com.console.app.main.model.Console;
import com.console.app.main.model.ExecutionResult;
import com.console.app.main.repository.ExecutionResultRepository;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ExecutionResultService {

    private final ExecutionResultRepository repository;
    private final ConsoleService consoleService;
    private final ExecutionResultCache cache;
    private static final Logger log = LoggerFactory.getLogger(ExecutionResultService.class);

    public ExecutionResultService(ExecutionResultRepository repository,
                                  ConsoleService consoleService,
                                  ExecutionResultCache cache) {
        this.repository = repository;
        this.consoleService = consoleService;
        this.cache = cache;
    }

    // Получение всех результатов
    public List<ExecutionResult> getAllExecutions() {
        List<ExecutionResult> results = repository.findAll();
        results.forEach(cache::put);
        return results;
    }

    // Получение по ID с кэшированием
    public ExecutionResult getExecutionById(Long id) {
        return cache.get(id)
                .map(result -> {
                    log.info("Cache for id: {}", id);
                    return result;
                })
                .orElseGet(() -> repository.findById(id)
                        .map(result -> {
                            cache.put(result);
                            return result;
                        })
                        .orElseThrow(() ->
                                new ExecutionNotFoundException("Execution not found with id: "
                                        + id))
                );
    }

    // Создание нового результата
    @Transactional
    public ExecutionResult createExecution(String language, String code,
                                           String result, Long consoleId) {
        Console console = consoleService.getConsoleById(consoleId);
        ExecutionResult executionResult = ExecutionResult.builder()
                .language(language)
                .code(code)
                .result(result)
                .console(console)
                .time(LocalDateTime.now())
                .build();

        ExecutionResult saved = repository.save(executionResult);
        cache.put(saved);
        return saved;
    }

    // Полное обновление
    @Transactional
    public ExecutionResult updateExecution(Long id, ExecutionResult executionResult) {
        return repository.findById(id)
                .map(existing -> {
                    updateExecutionFields(existing, executionResult);
                    ExecutionResult updated = repository.save(existing);
                    cache.put(updated);
                    return updated;
                })
                .orElseThrow(() ->
                        new ExecutionNotFoundException("Execution not found with id: " + id));
    }

    // Частичное обновление через PATCH
    @Transactional
    public ExecutionResult patchExecution(Long id, Map<String, Object> updates) {
        return repository.findById(id)
                .map(existing -> {
                    applyPartialUpdates(existing, updates);
                    ExecutionResult updated = repository.save(existing);
                    cache.put(updated);
                    return updated;
                })
                .orElseThrow(() ->
                        new ExecutionNotFoundException("Execution not found with id: " + id));
    }

    // Специальный метод для обновления времени
    @Transactional
    public ExecutionResult updateExecutionTime(Long id, LocalDateTime newTime) {
        return repository.findById(id)
                .map(existing -> {
                    existing.setTime(newTime);
                    ExecutionResult updated = repository.save(existing);
                    cache.put(updated);
                    return updated;
                })
                .orElseThrow(() ->
                        new ExecutionNotFoundException("Execution not found with id: " + id));
    }

    // Автоматическое обновление времени
    @Transactional
    public ExecutionResult touchExecution(Long id) {
        return updateExecutionTime(id, LocalDateTime.now());
    }

    // Удаление
    @Transactional
    public void deleteExecution(Long id) {
        repository.findById(id).ifPresent(execution -> {
            cache.evictFromCache(id);
            repository.delete(execution);
        });
    }

    // Вспомогательные методы
    private void updateExecutionFields(ExecutionResult target, ExecutionResult source) {
        target.setLanguage(source.getLanguage());
        target.setCode(source.getCode());
        target.setResult(source.getResult());
        target.setTime(source.getTime() != null ? source.getTime() : LocalDateTime.now());

        if (source.getConsole() != null) {
            target.setConsole(source.getConsole());
        }
    }

    @PostConstruct
    public void init() {
        cache.registerEvictionListener(new ExecutionResultCache.CacheEvictionListener() {
            @Override
            public void onEvict(Long key, ExecutionResult value) {
                // Можно добавить логирование или другую обработку
                System.out.println("Evicted from cache: " + key);
            }
        });
    }

    private void applyPartialUpdates(ExecutionResult target, Map<String, Object> updates) {
        updates.forEach((key, value) -> {
            switch (key) {
                case "language" -> target.setLanguage((String) value);
                case "code" -> target.setCode((String) value);
                case "result" -> target.setResult((String) value);
                case "time" -> target.setTime(parseTime(value));
                case "consoleId" ->
                        target.setConsole(consoleService
                                .getConsoleById(Long.parseLong(value.toString())));
                default -> log.warn("Unknown field '{}' for update in ExecutionResult", key);
            }
        });

        // Всегда обновляем время изменения
        target.setTime(LocalDateTime.now());
    }

    private LocalDateTime parseTime(Object value) {
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        return LocalDateTime.parse(value.toString());
    }
}
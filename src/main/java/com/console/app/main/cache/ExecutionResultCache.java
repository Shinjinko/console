package com.console.app.main.cache;

import com.console.app.main.model.ExecutionResult;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class ExecutionResultCache {
    private static final int MAX_CACHE_SIZE = 4;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    // Основное хранилище кэша
    private final LinkedHashMap<Long, ExecutionResult> cache = new LinkedHashMap<>(
            MAX_CACHE_SIZE * 4 / 3,
            0.75f,
            true
    ) {
        @Override
        protected boolean removeEldestEntry(Map.Entry<Long, ExecutionResult> eldest) {
            boolean shouldRemove = size() > MAX_CACHE_SIZE;
            if (shouldRemove) {
                onEntryEvicted(eldest.getKey(), eldest.getValue());
            }
            return shouldRemove;
        }
    };

    public void evictFromCache(Long id) {
        lock.writeLock().lock();
        try {
            cache.remove(id);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Добавим метод для обновления записи в кэше
    public void updateInCache(ExecutionResult result) {
        if (result == null) {
            return;
        }

        lock.writeLock().lock();
        try {
            ExecutionResult copy = deepCopy(result);
            cache.put(copy.getExecutionId(), copy);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Коллекция для хранения слушателей событий
    private final List<CacheEvictionListener> evictionListeners = new ArrayList<>();

    public interface CacheEvictionListener {
        void onEvict(Long key, ExecutionResult value);
    }

    // Регистрация слушателей
    public void registerEvictionListener(CacheEvictionListener listener) {
        lock.writeLock().lock();
        try {
            evictionListeners.add(listener);
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Обработка удаления элемента
    private void onEntryEvicted(Long key, ExecutionResult value) {
        lock.readLock().lock();
        try {
            evictionListeners.forEach(listener -> listener.onEvict(key, value));
        } finally {
            lock.readLock().unlock();
        }
    }

    // Основные операции кэша
    public void put(ExecutionResult result) {
        if (result == null) {
            return;
        }

        lock.writeLock().lock();
        try {
            ExecutionResult copy = deepCopy(result);
            cache.put(copy.getExecutionId(), copy);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public Optional<ExecutionResult> get(Long id) {
        lock.readLock().lock();
        try {
            ExecutionResult result = cache.get(id);
            return Optional.ofNullable(result != null ? deepCopy(result) : null);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void remove(Long id) {
        lock.writeLock().lock();
        try {
            cache.remove(id);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void clear() {
        lock.writeLock().lock();
        try {
            cache.clear();
        } finally {
            lock.writeLock().unlock();
        }
    }

    // Методы для получения информации о кэше
    public Map<Long, CachedExecutionInfo> getCacheContents() {
        lock.readLock().lock();
        try {
            return cache.entrySet().stream()
                    .collect(Collectors.toMap(
                            Map.Entry::getKey,
                            e -> new CachedExecutionInfo(
                                    e.getValue().getResult(),
                                    e.getValue().getTime(),
                                    e.getValue().getLanguage()
                            )
                    ));
        } finally {
            lock.readLock().unlock();
        }
    }

    public CacheStats getCacheStats() {
        lock.readLock().lock();
        try {
            double avgResultLength = cache.values().stream()
                    .mapToInt(e -> e.getResult().length())
                    .average()
                    .orElse(0);

            return new CacheStats(
                    cache.size(),
                    MAX_CACHE_SIZE,
                    avgResultLength,
                    System.currentTimeMillis()
            );
        } finally {
            lock.readLock().unlock();
        }
    }

    // Вспомогательные методы
    private ExecutionResult deepCopy(ExecutionResult original) {
        return ExecutionResult.builder()
                .executionId(original.getExecutionId())
                .language(original.getLanguage())
                .code(original.getCode())
                .result(original.getResult())
                .time(original.getTime())
                .console(original.getConsole())
                .build();
    }

    // DTO для информации о записях кэша
    public static class CachedExecutionInfo {
        private final String result;
        private final LocalDateTime time;
        private final String language;

        public CachedExecutionInfo(String result, LocalDateTime time, String language) {
            this.result = result;
            this.time = time;
            this.language = language;
        }

        // Геттеры
        public String getResult() {
            return result;
        }

        public LocalDateTime getTime() {
            return time;
        }

        public String getLanguage() {
            return language;
        }
    }

    // Расширенная статистика кэша
    public record CacheStats(
            int currentSize,
            int maxSize,
            double averageResultLength,
            long timestamp
    ) {}
}
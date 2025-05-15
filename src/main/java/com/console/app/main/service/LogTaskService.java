package com.console.app.main.service;

import com.console.app.main.exceptions.LogFileNotReadyException;
import com.console.app.main.model.LogTask;
import com.console.app.main.repository.LogTaskRepository;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class LogTaskService {

    private static final Logger log = LoggerFactory.getLogger(LogTaskService.class);
    private final LogTaskRepository logTaskRepository;
    private final String logFilePath;

    public LogTaskService(LogTaskRepository logTaskRepository,
                          @Value("${logging.file.name}") String logFilePath) {
        this.logTaskRepository = logTaskRepository;
        this.logFilePath = logFilePath;
    }

    @Async
    public void createLogFileAsync(UUID taskId, LocalDate date) {
        LogTask task = logTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalStateException("Task not found: " + taskId));

        try {
            task.setStatus("PROCESSING");
            task.setMessage("Формирование файла начато");
            logTaskRepository.save(task);

            // Имитация долгой обработки
            Thread.sleep(20000);

            // Основная логика создания файла
            Path dailyLogsDir = Paths.get("daily-logs");
            if (!Files.exists(dailyLogsDir)) {
                Files.createDirectories(dailyLogsDir);
            }

            String filename = "logs-" + date + "-" + taskId + ".log";
            Path logFile = dailyLogsDir.resolve(filename);
            Files.write(logFile, List.of("Пример логов за " + date));

            task.setStatus("COMPLETED");
            task.setFilePath(logFile.toString());
            task.setMessage("Файл успешно создан");
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            task.setStatus("FAILED");
            task.setMessage("Прервано пользователем");
        } catch (Exception e) {
            task.setStatus("FAILED");
            task.setMessage("Ошибка: " + e.getMessage());
        } finally {
            logTaskRepository.save(task);
        }
    }

    public LogTask createLogTask(LocalDate date) {
        LogTask task = new LogTask();
        task.setId(UUID.randomUUID());
        task.setStatus("PENDING");
        task.setDate(date);
        task.setCreatedAt(LocalDateTime.now());
        return logTaskRepository.save(task);
    }

    public LogTask getTaskStatus(UUID taskId) {
        return logTaskRepository.findById(taskId)
                .orElseThrow(() -> new IllegalArgumentException("Task not found: " + taskId));
    }

    public Path getLogFilePath(UUID taskId) {
        LogTask task = getTaskStatus(taskId);
        if (!"COMPLETED".equals(task.getStatus())) {
            throw new LogFileNotReadyException("Log file is not ready yet");
        }
        if (task.getFilePath() == null) {
            throw new IllegalStateException("Log file path is null");
        }
        return Paths.get(task.getFilePath());
    }
}
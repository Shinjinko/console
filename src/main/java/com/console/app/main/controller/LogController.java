package com.console.app.main.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/logs")
public class LogController {
    @Value("${logging.file.name}")
    private String logFilePath;

    @Operation(summary = "Get logs by date",
            description = "Retrieves and saves logs filtered by a specific date")
    @GetMapping("/by-date")
    public ResponseEntity<String> getLogsByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        try {
            // Читаем все строки из основного лог-файла
            List<String> filteredLogs = Files.readAllLines(Paths.get(logFilePath))
                    .stream()
                    .filter(line -> line.contains(date.toString()))
                    .collect(Collectors.toList());

            // Если нет логов за указанную дату
            if (filteredLogs.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body("No logs found for date: " + date);
            }

            // Создаем директорию для дневных логов, если ее нет
            Path dailyLogsDir = Paths.get("daily-logs");
            if (!Files.exists(dailyLogsDir)) {
                Files.createDirectory(dailyLogsDir);
            }

            // Формируем имя файла для логов этой даты
            String dailyLogFileName = "logs-" + date + ".log";
            Path dailyLogPath = dailyLogsDir.resolve(dailyLogFileName);

            // Записываем логи в отдельный файл
            Files.write(dailyLogPath, filteredLogs);

            // Возвращаем абсолютный путь к созданному файлу
            return ResponseEntity.ok("Logs saved to: " + dailyLogPath.toAbsolutePath());

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error processing logs: " + e.getMessage());
        }
    }
}
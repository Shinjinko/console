package com.console.app.main.service;

import com.console.app.main.model.Console;
import com.console.app.main.model.ExecutionResult;
import com.console.app.main.repository.ConsoleRepository;
import com.console.app.main.repository.ExecutionResultRepository;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

@Service
public class ConsoleService {
    private final ConsoleRepository consoleRepository;
    private final ExecutionResultRepository executionResultRepository; // Добавляем зависимость

    private static final String[] STATUS_MESSAGES = {
        "Выполнено успешно",
        "Ошибка компилятора",
        "Ошибка линковки",
        "Синтаксическая ошибка"
    };

    // Конструктор с двумя зависимостями
    public ConsoleService(ConsoleRepository consoleRepository,
                          ExecutionResultRepository executionResultRepository) {
        this.consoleRepository = consoleRepository;
        this.executionResultRepository = executionResultRepository;
    }

    // Методы работы с консолью
    public List<Console> getAllConsoles() {
        return consoleRepository.findAll();
    }

    public Console getConsoleById(Long id) {
        return consoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Console not found"));
    }

    public Console createConsole(Console console) {
        return consoleRepository.save(console);
    }

    public Console updateConsole(Long id, Console console) {
        console.setId(id);
        return consoleRepository.save(console);
    }

    public void deleteConsole(Long id) {
        consoleRepository.deleteById(id);
    }

    // Исполнение кода с привязкой к консоли
    public ExecutionResult executeCode(String language, String code, Long consoleId) {
        int randomIndex = ThreadLocalRandom.current().nextInt(STATUS_MESSAGES.length);
        String message = STATUS_MESSAGES[randomIndex];

        // Получаем консоль по ID
        Console console = getConsoleById(consoleId);

        // Создаем объект ExecutionResult с привязкой к консоли
        ExecutionResult result = new ExecutionResult(language, code, message, console);

        // Сохраняем результат выполнения в репозитории
        return executionResultRepository.save(result);
    }
}
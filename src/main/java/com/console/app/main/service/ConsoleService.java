package com.console.app.main.service;

import com.console.app.main.exceptions.SessionNotFoundException;
import com.console.app.main.model.Console;
import com.console.app.main.model.ExecutionResult;
import com.console.app.main.model.Session;
import com.console.app.main.repository.ConsoleRepository;
import com.console.app.main.service.SessionService;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

@Service
public class ConsoleService {
    private final ConsoleRepository consoleRepository;
    private final SessionService sessionService;

    private static final String[] STATUS_MESSAGES = {
        "Выполнено успешно",
        "Ошибка компилятора",
        "Ошибка линковки",
        "Синтаксическая ошибка"
    };

    // Конструктор с двумя зависимостями
    public ConsoleService(ConsoleRepository consoleRepository, SessionService sessionService) {
        this.consoleRepository = consoleRepository;
        this.sessionService = sessionService;
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

    // Исполнение кода с привязкой к сессии
    public ExecutionResult executeCode(String language, String code, Long userId) {
        int randomIndex = ThreadLocalRandom.current().nextInt(STATUS_MESSAGES.length);
        String message = STATUS_MESSAGES[randomIndex];

        // Получаем текущую активную сессию пользователя
        Session session = sessionService.getActiveSessionByUserId(userId)
                .orElseThrow(() -> new RuntimeException("No active session found for user"));

        // Создаем объект ExecutionResult с привязкой к сессии
        ExecutionResult result = new ExecutionResult(language, code, message, session);
        return result;
    }
}

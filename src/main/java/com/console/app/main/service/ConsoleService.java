package com.console.app.main.service;

import com.console.app.main.model.Console;
import com.console.app.main.model.ExecutionResult;
import com.console.app.main.repository.ConsoleRepository;
import com.console.app.main.repository.ExecutionResultRepository;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

@Service
public class ConsoleService {
    private final ConsoleRepository consoleRepository;
    private final ExecutionResultRepository executionResultRepository;
    private final HistoryService historyService;


    static final String[] STATUS_MESSAGES = {
        "Выполнено успешно",
        "Ошибка компилятора",
        "Ошибка линковки",
        "Синтаксическая ошибка"
    };

    // Конструктор с двумя зависимостями
    public ConsoleService(ConsoleRepository consoleRepository,
                          ExecutionResultRepository executionResultRepository,
                          HistoryService historyService) {
        this.consoleRepository = consoleRepository;
        this.executionResultRepository = executionResultRepository;
        this.historyService = historyService;
    }

    // Методы работы с консолью
    public List<Console> getAllConsoles() {
        return consoleRepository.findAll();
    }

    public Console getConsoleById(Long id) {
        return consoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Console not found"));
    }

    @Transactional(readOnly = true)
    public Optional<Console> getconsolebyiD(Long id) {
        return consoleRepository.findById(id);
    }

    public Console createConsole(Console console) {
        Console saved = consoleRepository.save(console);
        historyService.logAction(console.getId(), "Создана консоль: " + saved.getName());
        return saved;

    }

    public Console updateConsole(Long id, Console console) {
        Console existingConsole = consoleRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Console not found"));

        existingConsole.setName(console.getName());
        existingConsole.setType(console.getType());

        return consoleRepository.save(existingConsole);
    }


    public List<Console> createConsolesBulk(List<Console> consoles) {
        return consoles.stream()
                .filter(console -> console.getName() != null && !console.getName().isEmpty())
                .filter(console -> console.getType() != null && !console.getType().isEmpty())
                .map(consoleRepository::save)
                .toList();
    }

    public void deleteConsole(Long id) {
        consoleRepository.deleteById(id);
    }


    public ExecutionResult executeCode(String language, String code, Long consoleId) {
        int randomIndex = ThreadLocalRandom.current().nextInt(STATUS_MESSAGES.length);
        String message = STATUS_MESSAGES[randomIndex];

        Console console = getConsoleById(consoleId);
        ExecutionResult result = new ExecutionResult(language, code, message, console);

        return executionResultRepository.save(result);
    }

    @Transactional
    public Console save(Console console) {
        return consoleRepository.save(console);
    }
}
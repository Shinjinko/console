package com.console.app.main.service;

import com.console.app.main.model.HistoryItem;
import com.console.app.main.model.User;
import com.console.app.main.repository.HistoryItemRepository;
import com.console.app.main.repository.UserRepository;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
public class HistoryService {
    private final HistoryItemRepository historyItemRepository;
    private final UserRepository userRepository;

    public HistoryService(HistoryItemRepository historyItemRepository,
                          UserRepository userRepository) {
        this.historyItemRepository = historyItemRepository;
        this.userRepository = userRepository;
    }

    public List<HistoryItem> getUserHistory() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return historyItemRepository.findByUserEmailOrderByCreatedAtDesc(email);
    }

    public HistoryItem logAction(String description) {
        // Получаем email из контекста безопасности
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String email = authentication.getName();

        // Ищем пользователя по email
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        HistoryItem item = new HistoryItem();
        item.setDescription(description);
        item.setUser(user);

        return historyItemRepository.save(item);
    }
}
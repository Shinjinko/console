package com.console.app.main.service;

import com.console.app.main.model.HistoryItem;
import com.console.app.main.model.User;
import com.console.app.main.repository.HistoryItemRepository;
import com.console.app.main.repository.UserRepository;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
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

    @Transactional
    public void logAction(Long userId, String description) {
        User user = userRepository.findById(Math.toIntExact(userId))
                .orElseThrow(() -> new RuntimeException("User not found"));
        HistoryItem historyItem = new HistoryItem();
        historyItem.setUser(user);
        historyItem.setDescription(description);
        historyItem.setCreatedAt(LocalDateTime.now());
        historyItemRepository.save(historyItem);
    }

    public List<HistoryItem> getHistoryByUserId(Long userId) {
        return historyItemRepository.findByUser_IdOrderByCreatedAtDesc(userId);
    }
}
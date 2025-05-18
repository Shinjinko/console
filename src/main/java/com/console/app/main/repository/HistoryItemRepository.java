package com.console.app.main.repository;

import com.console.app.main.model.HistoryItem;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoryItemRepository extends JpaRepository<HistoryItem, Long> {
    List<HistoryItem> findByUserEmailOrderByCreatedAtDesc(String email);

    List<HistoryItem> findByUser_IdOrderByCreatedAtDesc(Long userId);
}


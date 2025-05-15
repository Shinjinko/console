package com.console.app.main.repository;

import com.console.app.main.model.HistoryItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoryItemRepository extends JpaRepository<HistoryItem, Long> {
    List<HistoryItem> findByUserEmailOrderByCreatedAtDesc(String email);
}
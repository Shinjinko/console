package com.console.app.main.repository;

import com.console.app.main.model.LogTask;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LogTaskRepository extends JpaRepository<LogTask, UUID> {
}
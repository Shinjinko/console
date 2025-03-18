package com.console.app.main.repository;

import com.console.app.main.model.Console;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsoleRepository extends JpaRepository<Console, Long> {
    // При необходимости можно добавить кастомные методы
}
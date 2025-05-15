package com.console.app.main.repository;

import com.console.app.main.model.User;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Integer> {
    // Метод для поиска по имени (без учета регистра)
    Optional<User> findByNameIgnoreCase(String name);

    Optional<User> findByEmail(String email); // Добавьте эту строку

}
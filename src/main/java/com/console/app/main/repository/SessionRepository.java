package com.console.app.main.repository;

import com.console.app.main.model.Session;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    @Query("SELECT s FROM Session s WHERE s.user.id = :userId AND s.status = 'ACTIVE'")
    Optional<Session> findActiveSessionByUserId(Long userId);
}
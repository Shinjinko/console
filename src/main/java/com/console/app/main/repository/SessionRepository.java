package com.console.app.main.repository;

import com.console.app.main.model.Session;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, Long> {
    @Query("SELECT s FROM Session s WHERE s.user.id = :userId AND s.status = 'ACTIVE'")
    Optional<Session> findActiveSessionByUserId(Long userId);

    @Query("SELECT s FROM Session s JOIN FETCH s.user u WHERE u.name = :name")
    List<Session> findAllByUserName(@Param("name") String name);
}
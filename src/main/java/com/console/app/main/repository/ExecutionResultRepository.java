package com.console.app.main.repository;

import com.console.app.main.model.ExecutionResult;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ExecutionResultRepository extends JpaRepository<ExecutionResult, Long> {
    // JPQL запрос с фильтрацией по вложенной сущности Console
    @Query("SELECT er FROM ExecutionResult er WHERE er.console.type = :consoleType"
            +
            " AND er.time BETWEEN :startDate AND :endDate")
    List<ExecutionResult> findByConsoleTypeAndTimeBetween(
            @Param("consoleType") String consoleType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    // Native SQL запрос с аналогичной фильтрацией
    @Query(value = "SELECT * FROM execution_results er "
            +
            "JOIN consoles c ON er.console_id = c.id "
            +
            "WHERE c.type = :consoleType AND er.time BETWEEN :startDate AND :endDate",
            nativeQuery = true)
    List<ExecutionResult> findByConsoleTypeAndTimeBetweenNative(
            @Param("consoleType") String consoleType,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate);

    @Query("SELECT er FROM ExecutionResult er WHERE er.time >= :dateAfter")
    List<ExecutionResult> findAllAfterDate(@Param("dateAfter") LocalDateTime dateAfter);

    // Native SQL запрос для той же фильтрации
    @Query(value = "SELECT * FROM execution_results WHERE time >= :dateAfter", nativeQuery = true)
    List<ExecutionResult> findAllAfterDateNative(@Param("dateAfter") LocalDateTime dateAfter);

    @Override
    @Modifying
    @Query("DELETE FROM ExecutionResult e WHERE e.executionId = :id")
    void deleteById(@Param("id") Long id);

    @Override
    @Transactional
    default void delete(ExecutionResult entity) {
        deleteById(entity.getExecutionId());
    }

    @Override
    @Transactional
    default void deleteAll(Iterable<? extends ExecutionResult> entities) {
        entities.forEach(this::delete);
    }
}
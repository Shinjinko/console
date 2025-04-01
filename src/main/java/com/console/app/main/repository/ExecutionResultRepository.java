package com.console.app.main.repository;

import com.console.app.main.model.ExecutionResult;
import com.console.app.main.model.Session;
import jakarta.transaction.Transactional;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

public interface ExecutionResultRepository extends JpaRepository<ExecutionResult, Long> {

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
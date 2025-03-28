package com.console.app.main.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(name = "execution_results")

public class ExecutionResult {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long executionId;

    @Column(nullable = false)
    private String language;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String code;

    @Column(nullable = false)
    private String result;

    @Column(nullable = false)
    private LocalDateTime time;

    // Связь многие-к-одному с Console
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "console_id", nullable = false)
    @JsonIgnore
    private Console console;

    @JsonProperty("console_id")
    public Long getUserId() {
        return console != null ? console.getId() : null;
    }

    public static ExecutionResultBuilder builder() {
        return new ExecutionResultBuilder();
    }

    // Добавляем Builder класс
    public static class ExecutionResultBuilder {
        private Long executionId;
        private String language;
        private String code;
        private String result;
        private LocalDateTime time;
        private Console console;

        ExecutionResultBuilder() {}

        public ExecutionResultBuilder executionId(Long executionId) {
            this.executionId = executionId;
            return this;
        }

        public ExecutionResultBuilder language(String language) {
            this.language = language;
            return this;
        }

        public ExecutionResultBuilder code(String code) {
            this.code = code;
            return this;
        }

        public ExecutionResultBuilder result(String result) {
            this.result = result;
            return this;
        }

        public ExecutionResultBuilder time(LocalDateTime time) {
            this.time = time;
            return this;
        }

        public ExecutionResultBuilder console(Console console) {
            this.console = console;
            return this;
        }

        public ExecutionResult build() {
            ExecutionResult result = new ExecutionResult();
            result.setExecutionId(this.executionId);
            result.setLanguage(this.language);
            result.setCode(this.code);
            result.setResult(this.result);
            result.setTime(this.time != null ? this.time : LocalDateTime.now());
            result.setConsole(this.console);
            return result;
        }
    }

    // Пустой конструктор (обязателен для JPA)
    public ExecutionResult() {}

    // Конструктор с параметрами
    public ExecutionResult(String language, String code, String result, Console console) {
        this.language = language;
        this.code = code;
        this.result = result;
        this.time = LocalDateTime.now();
        this.console = console;
    }

    // Геттеры и сеттеры
    public Long getExecutionId() {
        return executionId;
    }

    public void setExecutionId(Long executionId) {
        this.executionId = executionId;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Console getConsole() {
        return console;
    }

    public void setConsole(Console console) {
        this.console = console;
    }

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }
}


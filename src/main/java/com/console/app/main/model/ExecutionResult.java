package com.console.app.main.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    @JsonBackReference
    private Console console;

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


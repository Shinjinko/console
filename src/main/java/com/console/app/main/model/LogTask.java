package com.console.app.main.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "log_tasks")
@Getter
@Setter
public class LogTask {
    @Id
    private UUID id;
    private String status;
    private LocalDate date;
    private String filePath;
    private String message;
    private LocalDateTime createdAt;
}
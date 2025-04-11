package com.console.app.main.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Schema(description = "User entity")
@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "User ID", example = "1")
    private int id;

    @Column(nullable = false, unique = true)
    private String name;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // Связь один-ко-многим с Session
    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<Session> sessions = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "user_consoles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "console_id")
    )
    @JsonManagedReference
    private Set<Console> consoles = new HashSet<>();

    // Пустой конструктор (обязателен для JPA)
    public User() {}

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    // Геттеры и сеттеры
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Session> getSessions() {
        return sessions;
    }

    public void setSessions(List<Session> sessions) {
        this.sessions = sessions;
    }

    public Set<Console> getConsoles() {
        return consoles;
    }

    public void setConsoles(Set<Console> consoles) {
        this.consoles = consoles;
    }
}

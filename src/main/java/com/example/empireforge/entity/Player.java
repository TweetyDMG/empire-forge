package com.example.empireforge.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Players")
public class Player {

    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "username")
    private String username;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "player", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Company> companies = new ArrayList<>();

    public Player() {
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<Company> getCompanies() {
        return companies;
    }

    public void setCompanies(List<Company> companies) {
        this.companies = companies;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Player player = (Player) o;
        return Objects.equals(chatId, player.chatId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(chatId);
    }

    @Override
    public String toString() {
        return "Player{chatId=" + chatId + ", username='" + username + "'}";
    }
}

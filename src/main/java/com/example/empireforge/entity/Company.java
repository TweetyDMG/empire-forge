package com.example.empireforge.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Entity
@Table(name = "Companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "company_id")
    private Long companyId;

    @Column(name = "chat_id", nullable = false, insertable = false, updatable = false)
    private Long chatId;

    /**
     * Many-to-One связь с Player. Поле chatId остаётся для прямых записей/чтения,
     * player — для навигации по JPA-отношениям.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chat_id")
    private Player player;

    @Column(name = "company_type", nullable = false)
    private String companyType; // SERVICES or PRODUCTION

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "company", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<GameState> gameStates = new ArrayList<>();

    public Company() {
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Long getChatId() {
        return chatId;
    }

    public void setChatId(Long chatId) {
        this.chatId = chatId;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public String getCompanyType() {
        return companyType;
    }

    public void setCompanyType(String companyType) {
        this.companyType = companyType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public List<GameState> getGameStates() {
        return gameStates;
    }

    public void setGameStates(List<GameState> gameStates) {
        this.gameStates = gameStates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Company company = (Company) o;
        return Objects.equals(companyId, company.companyId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(companyId);
    }

    @Override
    public String toString() {
        return "Company{id=" + companyId + ", name='" + name + "', type=" + companyType + "}";
    }
}

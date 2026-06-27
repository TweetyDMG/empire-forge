package com.example.empireforge.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.Objects;

@Entity
@Table(name = "GameState")
public class GameState {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "state_id")
    private Long stateId;

    @Column(name = "company_id", nullable = false, insertable = false, updatable = false)
    private Long companyId;

    /**
     * Many-to-One связь с Company. Поле companyId остаётся для прямых записей/чтения,
     * company — для навигации по JPA-отношениям.
     */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(nullable = false)
    private int balance = 10000;

    @Column(nullable = false)
    private int employees = 5;

    @Column
    private int reputation = 50;

    @Column
    private int resources = 100;

    @Column(nullable = false)
    private int demand = 50;

    @Column(nullable = false)
    private int competition = 30;

    @Column(name = "tax_rate", nullable = false)
    private int taxRate = 10;

    @Column(name = "service_quality")
    private int serviceQuality = 50;

    @Column(name = "equipment_level")
    private int equipmentLevel = 1;

    @Column(name = "current_day", nullable = false)
    private int currentDay = 1;

    @Column(name = "last_updated", nullable = false)
    private LocalDateTime lastUpdated = LocalDateTime.now();

    public GameState() {
    }

    public Long getStateId() {
        return stateId;
    }

    public void setStateId(Long stateId) {
        this.stateId = stateId;
    }

    public Long getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Long companyId) {
        this.companyId = companyId;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public int getBalance() {
        return balance;
    }

    public void setBalance(int balance) {
        this.balance = balance;
    }

    public int getEmployees() {
        return employees;
    }

    public void setEmployees(int employees) {
        this.employees = employees;
    }

    public int getReputation() {
        return reputation;
    }

    public void setReputation(int reputation) {
        this.reputation = reputation;
    }

    public int getResources() {
        return resources;
    }

    public void setResources(int resources) {
        this.resources = resources;
    }

    public int getDemand() {
        return demand;
    }

    public void setDemand(int demand) {
        this.demand = demand;
    }

    public int getCompetition() {
        return competition;
    }

    public void setCompetition(int competition) {
        this.competition = competition;
    }

    public int getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(int taxRate) {
        this.taxRate = taxRate;
    }

    public int getServiceQuality() {
        return serviceQuality;
    }

    public void setServiceQuality(int serviceQuality) {
        this.serviceQuality = serviceQuality;
    }

    public int getEquipmentLevel() {
        return equipmentLevel;
    }

    public void setEquipmentLevel(int equipmentLevel) {
        this.equipmentLevel = equipmentLevel;
    }

    public int getCurrentDay() {
        return currentDay;
    }

    public void setCurrentDay(int currentDay) {
        this.currentDay = currentDay;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated = lastUpdated;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GameState gameState = (GameState) o;
        return Objects.equals(stateId, gameState.stateId);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(stateId);
    }

    @Override
    public String toString() {
        return "GameState{id=" + stateId + ", day=" + currentDay + ", balance=" + balance + "}";
    }
}

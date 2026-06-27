package com.example.empireforge.dto;

/**
 * DTO для отображения состояния компании (GameState).
 */
public record GameStatusResponse(
        String companyName,
        String companyType,
        int day,
        int balance,
        int employees,
        int demand,
        int reputation,
        int resources,
        int competition,
        int serviceQuality,
        int equipmentLevel,
        int taxRate
) {

    public String toTelegramMessage() {
        return String.format("""
                🏢 Компания: %s (%s)
                📅 День: %d
                💰 Баланс: %d у.е.
                👥 Сотрудники: %d
                📈 Спрос: %d%%
                ⭐ Репутация: %d
                📦 Ресурсы: %d
                🏭 Конкуренция: %d%%
                💎 Качество услуг: %d
                🔧 Уровень оборудования: %d
                💸 Налог: %d%%""",
                companyName, companyType, day, balance, employees,
                demand, reputation, resources, competition,
                serviceQuality, equipmentLevel, taxRate);
    }
}

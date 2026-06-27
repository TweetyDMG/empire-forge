package com.example.empireforge.exception;

/**
 * Исключение — игровое состояние не найдено для данной компании.
 */
public class GameStateNotFoundException extends GameException {

    private final Long companyId;

    public GameStateNotFoundException(Long companyId) {
        super("Состояние игры не найдено для companyId=" + companyId);
        this.companyId = companyId;
    }

    public Long getCompanyId() {
        return companyId;
    }
}

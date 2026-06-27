package com.example.empireforge.dto;

/**
 * DTO для ответов игровой логики.
 * Заменяет сырые строки в API-слое.
 */
public record GameResponse(String message) {

    public static GameResponse of(String message) {
        return new GameResponse(message);
    }

    public static GameResponse of(String format, Object... args) {
        return new GameResponse(String.format(format, args));
    }
}

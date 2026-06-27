package com.example.empireforge.exception;

/**
 * Базовое исключение игровой логики EmpireForge.
 * Все кастомные исключения проекта наследуются от него.
 */
public class GameException extends RuntimeException {

    public GameException(String message) {
        super(message);
    }

    public GameException(String message, Throwable cause) {
        super(message, cause);
    }
}

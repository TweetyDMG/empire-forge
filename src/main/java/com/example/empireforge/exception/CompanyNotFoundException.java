package com.example.empireforge.exception;

/**
 * Исключение — компания не найдена для данного chatId.
 */
public class CompanyNotFoundException extends GameException {

    private final Long chatId;

    public CompanyNotFoundException(Long chatId) {
        super("Компания не найдена для chatId=" + chatId);
        this.chatId = chatId;
    }

    public Long getChatId() {
        return chatId;
    }
}

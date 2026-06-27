package com.example.empireforge.bot;

import com.example.empireforge.config.TelegramBotConfig;
import com.example.empireforge.dto.GameResponse;
import com.example.empireforge.dto.GameStatusResponse;
import com.example.empireforge.exception.GameException;
import com.example.empireforge.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import jakarta.annotation.PostConstruct;

@Component
public class EmpireForgeBot extends TelegramLongPollingBot {

    private static final Logger LOGGER = LoggerFactory.getLogger(EmpireForgeBot.class);

    private final GameService gameService;
    private final TelegramBotConfig botConfig;

    public EmpireForgeBot(GameService gameService, TelegramBotConfig botConfig) {
        this.gameService = gameService;
        this.botConfig = botConfig;
    }

    @PostConstruct
    public void init() {
        LOGGER.info("Initializing EmpireForgeBot with username: {}", botConfig.getUsername());
        try {
            this.getMe();
            LOGGER.info("Successfully connected to Telegram API");
            LOGGER.info("Long Polling enabled: {}", botConfig.isEnableLongPolling());

            execute(new org.telegram.telegrambots.meta.api.methods.updates.DeleteWebhook());
            LOGGER.info("Webhook cleared, using Long Polling");

            TelegramBotsApi botsApi = new TelegramBotsApi(DefaultBotSession.class);
            botsApi.registerBot(this);
            LOGGER.info("Bot successfully registered for Long Polling");
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to connect to Telegram API or register bot: {}", e.getMessage(), e);
        }
    }

    @Override
    public String getBotUsername() {
        return botConfig.getUsername();
    }

    @Override
    public String getBotToken() {
        return botConfig.getToken();
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (!update.hasMessage() || !update.getMessage().hasText()) {
            return;
        }

        Long chatId = update.getMessage().getChatId();
        String text = update.getMessage().getText().trim();
        String username = update.getMessage().getFrom().getUserName();

        LOGGER.info("Processing command: {} from user: {} in chat: {}", text, username, chatId);

        String response;
        try {
            response = handleCommand(chatId, username, text);
        } catch (GameException e) {
            LOGGER.warn("Game logic error: {}", e.getMessage());
            response = "⚠️ " + e.getMessage();
        } catch (Exception e) {
            LOGGER.error("Unexpected error processing command: {}", e.getMessage(), e);
            response = "❌ Произошла непредвиденная ошибка. Попробуйте позже.";
        }

        sendMessage(chatId, response);
    }

    private String handleCommand(Long chatId, String username, String text) {
        if (text.equals("/start")) {
            return "Добро пожаловать в EmpireForge!\n\n" +
                    "📋 Доступные команды:\n" +
                    "/startgame SERVICES <название> — создать компанию услуг\n" +
                    "/startgame PRODUCTION <название> — создать производственную компанию\n" +
                    "/status — состояние компании\n" +
                    "/endday — завершить день и получить прибыль";
        }

        if (text.startsWith("/startgame")) {
            String[] parts = text.split(" ", 3);
            String companyType = parts.length > 1 ? parts[1].toUpperCase() : "SERVICES";
            String companyName = parts.length > 2 ? parts[2] : "МояКомпания";

            if (!companyType.equals("SERVICES") && !companyType.equals("PRODUCTION")) {
                return "❌ Неверный тип компании! Используйте SERVICES или PRODUCTION.\n" +
                        "Пример: /startgame SERVICES МояФирма";
            }

            GameResponse result = gameService.startGame(chatId, username, companyType, companyName);
            return result.message();
        }

        if (text.equals("/status")) {
            GameStatusResponse status = gameService.getStatus(chatId);
            return status.toTelegramMessage();
        }

        if (text.equals("/endday")) {
            GameResponse result = gameService.endDay(chatId);
            return result.message();
        }

        return "❓ Неизвестная команда.\n\nДоступные команды:\n" +
                "/start — приветствие и список команд\n" +
                "/startgame SERVICES <название> — создать компанию\n" +
                "/status — состояние компании\n" +
                "/endday — завершить день";
    }

    private void sendMessage(Long chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId.toString());
        message.setText(text);
        try {
            execute(message);
            LOGGER.debug("Sent message to chat {}: {}", chatId, text);
        } catch (TelegramApiException e) {
            LOGGER.error("Failed to send message to chat {}: {}", chatId, e.getMessage(), e);
        }
    }
}

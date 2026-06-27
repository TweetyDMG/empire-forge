package com.example.empireforge.bot;

import com.example.empireforge.config.TelegramBotConfig;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
public class TelegramWebhookController {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramWebhookController.class);
    private static final String SECRET_TOKEN_HEADER = "X-Telegram-Bot-Api-Secret-Token";

    private final EmpireForgeBot bot;
    private final TelegramBotConfig botConfig;

    public TelegramWebhookController(EmpireForgeBot bot, TelegramBotConfig botConfig) {
        this.bot = bot;
        this.botConfig = botConfig;
    }

    @PostMapping("/telegram")
    public ResponseEntity<String> receiveUpdate(
            @RequestBody Update update,
            HttpServletRequest request) {

        // Верификация secret token (если настроен)
        String configuredSecret = botConfig.getSecretToken();
        if (configuredSecret != null && !configuredSecret.isBlank()) {
            String requestSecret = request.getHeader(SECRET_TOKEN_HEADER);
            if (!configuredSecret.equals(requestSecret)) {
                LOGGER.warn("Webhook rejected: invalid or missing secret token");
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Unauthorized");
            }
        }

        LOGGER.debug("Received webhook update: {}", update.getUpdateId());
        bot.onUpdateReceived(update);
        return ResponseEntity.ok().body("OK");
    }
}

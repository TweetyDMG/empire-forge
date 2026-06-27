package com.example.empireforge.config;

import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "telegram.bot")
public class TelegramBotConfig {

    private static final Logger LOGGER = LoggerFactory.getLogger(TelegramBotConfig.class);

    private String username;
    private String token;
    private String webhookPath;
    private boolean enableLongPolling;
    private String secretToken;

    @PostConstruct
    public void logConfig() {
        if (username == null || username.isBlank()) {
            LOGGER.error("TELEGRAM_BOT_USERNAME is not configured!");
        }
        if (token == null || token.isBlank()) {
            LOGGER.error("TELEGRAM_BOT_TOKEN is not configured!");
        }
        LOGGER.info("Loaded TelegramBotConfig: username={}, webhookPath={}, enableLongPolling={}, secretToken={}",
                username,
                webhookPath != null && !webhookPath.isBlank() ? webhookPath : "N/A",
                enableLongPolling,
                secretToken != null && !secretToken.isBlank() ? "[CONFIGURED]" : "N/A");
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getWebhookPath() {
        return webhookPath;
    }

    public void setWebhookPath(String webhookPath) {
        this.webhookPath = webhookPath;
    }

    public boolean isEnableLongPolling() {
        return enableLongPolling;
    }

    public void setEnableLongPolling(boolean enableLongPolling) {
        this.enableLongPolling = enableLongPolling;
    }

    public String getSecretToken() {
        return secretToken;
    }

    public void setSecretToken(String secretToken) {
        this.secretToken = secretToken;
    }
}

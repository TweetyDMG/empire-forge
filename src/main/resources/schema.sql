CREATE TABLE Players (
                         chat_id BIGINT PRIMARY KEY,
                         username VARCHAR(255),
                         created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE Companies (
                           company_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           chat_id BIGINT NOT NULL,
                           company_type VARCHAR(20) NOT NULL,
                           name VARCHAR(255) NOT NULL,
                           created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (chat_id) REFERENCES Players(chat_id)
);

CREATE TABLE GameState (
                           state_id BIGINT PRIMARY KEY AUTO_INCREMENT,
                           company_id BIGINT NOT NULL,
                           balance INTEGER NOT NULL DEFAULT 10000,
                           employees INTEGER NOT NULL DEFAULT 5,
                           reputation INTEGER DEFAULT 50,
                           resources INTEGER DEFAULT 100,
                           demand INTEGER NOT NULL DEFAULT 50,
                           competition INTEGER NOT NULL DEFAULT 30,
                           tax_rate INTEGER NOT NULL DEFAULT 10,
                           service_quality INTEGER DEFAULT 50,
                           equipment_level INTEGER DEFAULT 1,
                           current_day INTEGER NOT NULL DEFAULT 1,
                           last_updated TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           FOREIGN KEY (company_id) REFERENCES Companies(company_id)
);
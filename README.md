# EmpireForge — Telegram Bot для экономической стратегии

Telegram-бот, реализующий симулятор управления компанией. Игрок создаёт бизнес (услуги или производство), управляет ресурсами, сотрудниками и финансами, принимает ежедневные решения в условиях динамичного рынка. Реализован на Java 23 + Spring Boot.

---

## 🛠 Технологический стек

При проектировании архитектуры приложения упор делался на надёжность хранения данных и модульность.

*   **Язык разработки:** Java 23
*   **Фреймворки:** Spring Boot 3.4.4 (Web, Data JPA, Validation)
*   **Базы данных:** H2 (In-Memory — dev), PostgreSQL (production — опционально)
*   **Кэширование и очереди:** не используются
*   **Контейнеризация и DevOps:** Docker, Docker Compose
*   **Инструменты тестирования:** JUnit (Spring Boot Test)

---

## 🚀 Ключевой функционал

*   **Управление пользователями:** Автоматическое создание профиля при старте игры
*   **Автоматизация логики:** Бизнес-симуляция — ежедневный цикл: доходы от спроса, расходы на зарплаты и налоги, динамический рыночный спрос и конкуренция
*   **Интеграции:** TelegramBots API (Long Polling + Webhook), верификация webhook через `X-Telegram-Bot-Api-Secret-Token`
*   **Валидация и безопасность:** Выбор типа бизнеса: `SERVICES` (услуги) или `PRODUCTION` (производство)

---

## 📁 Архитектура и структура проекта

В проекте используется классическая MVC-архитектура с паттерном Repository. Это обеспечивает независимость бизнес-логики от внешних библиотек и баз данных.

```text
EmpireForge/
├── Dockerfile                                  # Multi-stage сборка
├── docker-compose.yml                          # Docker Compose (prod/dev)
├── .env.example                                # Шаблон переменных окружения
├── .gitignore
├── pom.xml
├── src/main/
│   ├── java/com/example/empireforge/
│   │   ├── EmpireForgeApplication.java         # Точка входа Spring Boot
│   │   ├── bot/
│   │   │   ├── EmpireForgeBot.java             # Telegram Long Polling бот
│   │   │   └── TelegramWebhookController.java  # REST-контроллер webhook
│   │   ├── config/
│   │   │   └── TelegramBotConfig.java          # @ConfigurationProperties
│   │   ├── dto/
│   │   │   ├── GameResponse.java               # Generic ответ
│   │   │   └── GameStatusResponse.java         # Статус компании
│   │   ├── entity/
│   │   │   ├── Player.java                     # JPA: игрок
│   │   │   ├── Company.java                    # JPA: компания
│   │   │   └── GameState.java                  # JPA: игровое состояние
│   │   ├── exception/
│   │   │   ├── GameException.java              # Базовое исключение
│   │   │   ├── CompanyNotFoundException.java
│   │   │   └── GameStateNotFoundException.java
│   │   ├── repository/
│   │   │   ├── PlayerRepository.java
│   │   │   ├── CompanyRepository.java
│   │   │   └── GameStateRepository.java
│   │   └── service/
│   │       └── GameService.java                # Бизнес-логика
│   └── resources/
│       ├── application.properties
│       ├── application-local.properties
│       ├── logback-spring.xml
│       └── schema.sql                          # DDL-скрипт
└── src/test/
    ├── java/.../
    │   ├── EmpireForgeApplicationTests.java    # Контекст Spring
    │   └── service/GameServiceTest.java        # Юнит-тесты (6 тестов)
    └── resources/
        └── application.properties
```

---

## 💻 Локальное развертывание

Для запуска проекта в изолированном окружении вам понадобятся **Java 23**, **Maven 3.9+** и Telegram-бот, зарегистрированный через [@BotFather](https://t.me/BotFather).

### 1. Клонирование репозитория

```bash
git clone https://github.com/<ваш-username>/empireforge.git
cd empireforge
```

### 2. Настройка переменных окружения

Создайте файл `.env` в корневой директории проекта по образцу `.env.example`:

```env
TELEGRAM_BOT_USERNAME=my_company_bot
TELEGRAM_BOT_TOKEN=123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11
TELEGRAM_BOT_LONG_POLLING=true
```

### 3. Запуск

```bash
# Сборка
mvn clean package -DskipTests

# Запуск (Long Polling)
mvn spring-boot:run

# Запуск через Docker
docker compose --profile dev up -d
```

### 4. Тестирование

```bash
mvn test
```

---

## 🔌 Команды Telegram-бота

| Команда | Описание | Доступ |
|---------|----------|--------|
| `/start` | Приветствие и справка | Все |
| `/startgame SERVICES <name>` | Создать компанию сферы услуг | Зарегистрированные |
| `/startgame PRODUCTION <name>` | Создать производственную компанию | Зарегистрированные |
| `/status` | Текущее состояние компании | Владелец |
| `/endday` | Завершить день (доходы/расходы) | Владелец |

### REST API (webhook)

| Метод | Путь | Описание |
|-------|------|----------|
| `POST` | `/telegram` | Приём обновлений от Telegram |

---

## 👥 Разработчики

* [**Артем Рогачев**](https://github.com/TweetyDMG) — Backend Developer

## 📜 Лицензия

[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](https://opensource.org/licenses/MIT)

Проект распространяется на условиях лицензии **MIT**. Полный текст лицензии находится в файле [LICENSE](./LICENSE).

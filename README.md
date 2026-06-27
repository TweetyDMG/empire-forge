# EmpireForge — Telegram Bot для экономической стратегии

**EmpireForge** — это Telegram-бот, реализующий симулятор управления компанией. Игрок создаёт бизнес (услуги или производство), управляет ресурсами, сотрудниками и финансами, принимает ежедневные решения в условиях динамичного рынка.

---

## 🛠 Технологический стек

| Категория | Технология |
|-----------|------------|
| **Язык** | Java 23 |
| **Фреймворк** | Spring Boot 3.4.4 (Web, Data JPA, Validation) |
| **База данных** | H2 (In-Memory — dev), PostgreSQL (production — опционально) |
| **Telegram API** | TelegramBots 6.9.7.1 (Long Polling + Webhook) |
| **ORM** | Hibernate 6 / Jakarta Persistence 3.x |
| **Сборка** | Apache Maven |
| **Контейнеризация** | Docker / Docker Compose |
| **Логирование** | SLF4J + Logback 1.5.x |

---

## 🚀 Ключевой функционал

- **Регистрация игроков** — автоматическое создание профиля при старте игры
- **Создание компании** — выбор типа бизнеса: `SERVICES` (услуги) или `PRODUCTION` (производство)
- **Бизнес-симуляция** — ежедневный цикл: доходы от спроса, расходы на зарплаты и налоги
- **Просмотр состояния** — баланс, штат сотрудников, репутация, ресурсы, рыночный спрос, конкуренция
- **Два режима доставки обновлений** — Long Polling (LOCAL) и Webhook (PRODUCTION)
- **Защита webhook** — верификация через `X-Telegram-Bot-Api-Secret-Token`

---

## 📁 Архитектура и структура данных

### Модель базы данных

```
┌──────────────┐       ┌──────────────────┐       ┌──────────────────────────┐
│    Player    │       │     Company      │       │       GameState          │
├──────────────┤       ├──────────────────┤       ├──────────────────────────┤
│ chat_id (PK) │──1:N──│ company_id (PK)  │──1:1──│ state_id (PK)            │
│ username     │       │ chat_id (FK)     │       │ company_id (FK)          │
│ created_at   │       │ company_type     │       │ balance (default 10 000) │
└──────────────┘       │ name             │       │ employees (default 5)    │
                       │ created_at       │       │ reputation               │
                       └──────────────────┘       │ resources                │
                                                  │ demand                   │
                                                  │ competition              │
                                                  │ tax_rate                 │
                                                  │ service_quality          │
                                                  │ equipment_level          │
                                                  │ current_day              │
                                                  │ last_updated             │
                                                  └──────────────────────────┘
```

### Структура проекта

```
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
│   │   │   └── GameStatusResponse.java         # Статус компании (DTO)
│   │   ├── entity/
│   │   │   ├── Player.java                     # JPA: игрок
│   │   │   ├── Company.java                    # JPA: компания
│   │   │   └── GameState.java                  # JPA: игровое состояние
│   │   ├── exception/
│   │   │   ├── GameException.java              # Базовое исключение
│   │   │   ├── CompanyNotFoundException.java   # Компания не найдена
│   │   │   └── GameStateNotFoundException.java # Состояние не найдено
│   │   ├── repository/
│   │   │   ├── PlayerRepository.java
│   │   │   ├── CompanyRepository.java
│   │   │   └── GameStateRepository.java
│   │   └── service/
│   │       └── GameService.java                # Бизнес-логика
│   └── resources/
│       ├── application.properties              # Основная конфигурация
│       ├── application-local.properties         # Локальная (в .gitignore)
│       ├── logback-spring.xml                  # Логирование
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

### Требования

- **Java 23** (OpenJDK)
- **Apache Maven 3.9+**
- **Docker** (опционально)
- Telegram-бот, зарегистрированный через [@BotFather](https://t.me/BotFather)

### 1. Клонирование

```bash
git clone https://github.com/<your-org>/empireforge.git
cd empireforge
```

### 2. Настройка переменных окружения

Скопируйте `.env.example` в `.env` и заполните свои значения:

```bash
cp .env.example .env
```

Минимальный `.env`:

```ini
TELEGRAM_BOT_USERNAME=my_company_bot
TELEGRAM_BOT_TOKEN=123456:ABC-DEF1234ghIkl-zyx57W2v1u123ew11
TELEGRAM_BOT_LONG_POLLING=true
```

Также можно скопировать локальный конфиг (он уже в `.gitignore`):

```bash
cp src/main/resources/application-local.properties src/main/resources/application.properties
```

### 3. Сборка и запуск (Maven)

```bash
mvn clean package -DskipTests
mvn spring-boot:run
```

Сборка без сети (если зависимости уже в локальном репозитории):

```bash
mvn -o spring-boot:run
```

### 4. Запуск через Docker

```bash
# Собрать образ
docker build -t empireforge .

# Запустить
docker run --rm -p 8080:8080 \
  -e TELEGRAM_BOT_USERNAME="..." \
  -e TELEGRAM_BOT_TOKEN="..." \
  -e TELEGRAM_BOT_LONG_POLLING="true" \
  empireforge
```

### 5. Запуск через Docker Compose

```bash
docker compose --profile dev up -d
```

После запуска бот будет отвечать на команды в Telegram в режиме Long Polling.

### Тестирование

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
| `GET` | `/h2-console` | Консоль H2 (dev, по умолчанию отключена) |

---

## 🧪 Команды Maven

```bash
mvn clean compile           # Компиляция
mvn test                    # Запуск тестов
mvn package -DskipTests     # Сборка JAR
mvn spring-boot:run         # Запуск приложения
```

---

## 📄 Лицензия

MIT License — проект создан в образовательных целях.

# =============================================================================
# EmpireForge — Dockerfile
# Maven build → JAR → distroless Java runtime
# =============================================================================

# ---- Этап 1: сборка ----
FROM maven:3.9-eclipse-temurin-23-alpine AS builder
WORKDIR /build
COPY pom.xml .
RUN mvn dependency:go-offline -B
COPY src ./src
RUN mvn package -DskipTests -B

# ---- Этап 2: runtime ----
FROM eclipse-temurin:23-jre-alpine
WORKDIR /app

# Создаём непривилегированного пользователя
RUN addgroup -S appgroup && adduser -S appuser -G appgroup

# Копируем собранный JAR из builder
COPY --from=builder /build/target/*.jar app.jar

USER appuser

EXPOSE 8080

ENTRYPOINT ["java", \
    "-jar", "app.jar", \
    "--spring.profiles.active=${SPRING_PROFILES_ACTIVE:prod}"]

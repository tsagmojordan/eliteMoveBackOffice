# ── Étape 1 : Build Maven classique ─────────────────────────────────────────
FROM maven:3.9.6-eclipse-temurin-21 AS builder

WORKDIR /app
COPY pom.xml .
COPY src ./src

RUN mvn clean package -DskipTests

# ── Étape 2 : Image finale légère ────────────────────────────────────────────
FROM eclipse-temurin:21-jre-alpine

WORKDIR /app

COPY --from=builder /app/target/*.jar app.jar

EXPOSE 8080

# Optimisé pour conteneur avec peu de RAM (256MB Back4app)
ENTRYPOINT ["java", \
  "-Xms64m", \
  "-Xmx200m", \
  "-XX:+UseContainerSupport", \
  "-XX:MaxRAMPercentage=75.0", \
  "-jar", "app.jar"]

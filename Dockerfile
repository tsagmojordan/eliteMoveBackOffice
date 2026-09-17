# syntax=docker/dockerfile:1

# ===== Étape 1 : cache des dépendances Maven =====
FROM maven:3.9-eclipse-temurin-23-alpine AS dependencies
WORKDIR /app
COPY pom.xml .
RUN mvn -B -q dependency:go-offline

# ===== Étape 2 : build du jar =====
FROM maven:3.9-eclipse-temurin-23-alpine AS builder
WORKDIR /app
COPY --from=dependencies /root/.m2 /root/.m2
COPY pom.xml .
COPY src ./src
RUN mvn -B -q package -DskipTests

# ===== Étape 3 : extraction des layers Spring Boot =====
# Le jar est découpé en couches (dépendances / loader / application)
# pour que les dépendances ne soient rebuildées que si le pom change.
FROM eclipse-temurin:23-jre-alpine AS extractor
WORKDIR /app
COPY --from=builder /app/target/*.jar app.jar
RUN java -Djarmode=tools -jar app.jar extract --layers --launcher --destination extracted

# ===== Étape finale : image d'exécution =====
FROM eclipse-temurin:23-jre-alpine
WORKDIR /app

# Utilisateur non-root : le backend écrit ses uploads dans {user.dir}/upload/picture/vehicule
RUN addgroup -S spring && adduser -S spring -G spring \
    && mkdir -p upload/picture/vehicule/thumbnails \
    && chown -R spring:spring /app
COPY --from=extractor /app/extracted/dependencies/ ./
COPY --from=extractor /app/extracted/spring-boot-loader/ ./
COPY --from=extractor /app/extracted/snapshot-dependencies/ ./
COPY --from=extractor /app/extracted/application/ ./
USER spring

# Port attendu par docker-compose.yml (l'application lit ${PORT:8080})
ENV PORT=7820
EXPOSE 7820

HEALTHCHECK --interval=30s --timeout=5s --retries=5 --start-period=60s \
    CMD wget -qO- http://localhost:7820/actuator/health || exit 1

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
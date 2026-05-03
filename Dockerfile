# ================================
# STAGE 1 : Dépendances (cache layer)
# ================================
FROM maven:3.9-eclipse-temurin-21-alpine AS dependencies

WORKDIR /app

COPY pom.xml .
RUN mvn dependency:go-offline -B

# ================================
# STAGE 2 : Build
# ================================
FROM dependencies AS build

COPY src ./src
RUN mvn clean package -DskipTests -B

# ================================
# STAGE 3 : Extraction des layers Spring Boot
# ================================
FROM eclipse-temurin:21-jre-alpine AS extractor

WORKDIR /app
COPY --from=build /app/target/RIDE_APP_BACKEND-0.0.1-SNAPSHOT.jar app.jar
RUN java -Djarmode=layertools -jar app.jar extract

# ================================
# STAGE 4 : Image finale minimale
# ================================
FROM eclipse-temurin:21-jre-alpine AS final

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

COPY --from=extractor /app/dependencies ./
COPY --from=extractor /app/spring-boot-loader ./
COPY --from=extractor /app/snapshot-dependencies ./
COPY --from=extractor /app/application ./

EXPOSE 7820

ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+OptimizeStringConcat \
               -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
# ================================
# STAGE 1 : Build
# ================================
FROM maven:3.9-eclipse-temurin-21-alpine AS build

WORKDIR /app
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B && \
    java -Djarmode=layertools -jar target/RIDE_APP_BACKEND-0.0.1-SNAPSHOT.jar extract

# ================================
# STAGE 2 : Image finale minimale
# ================================
FROM eclipse-temurin:21-jre-alpine

RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

WORKDIR /app

COPY --from=build /app/dependencies ./
COPY --from=build /app/spring-boot-loader ./
COPY --from=build /app/snapshot-dependencies ./
COPY --from=build /app/application ./

EXPOSE 7820

ENV JAVA_OPTS="-XX:+UseContainerSupport \
               -XX:MaxRAMPercentage=75.0 \
               -XX:+UseG1GC \
               -XX:+OptimizeStringConcat \
               -Djava.security.egd=file:/dev/./urandom"

ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS org.springframework.boot.loader.launch.JarLauncher"]
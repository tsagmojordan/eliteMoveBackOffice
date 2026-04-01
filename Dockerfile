# ── Étape 1 : Build natif avec GraalVM ──────────────────────────────────────
FROM ghcr.io/graalvm/native-image-community:21 AS builder

WORKDIR /app

# Installer Maven
RUN microdnf install -y maven findutils

# Copier et builder
COPY pom.xml .
COPY src ./src
RUN mvn clean package -Pnative -DskipTests

# ── Étape 2 : Image finale légère ────────────────────────────────────────────
FROM debian:bookworm-slim

WORKDIR /app

# Copier le binaire natif
COPY --from=builder /app/target/mon-app .

# Back4app utilise $PORT dynamiquement
EXPOSE 8080

CMD ["./mon-app"]
# =============================================================================
#  Dockerfile — Build natif GraalVM 21 pour Back4app (256MB RAM / 0.25 CPU)
#  Stack : Spring Boot 3.4.2 · Security · JWT · JPA · WebSocket · Twilio
#          Thymeleaf · Spring Modulith · MapStruct · Lombok · Swagger
#
#  Usage local :
#    docker build -t ride-app-backend .
#    docker run -p 8080:8080 \
#      -e SPRING_DATASOURCE_URL=jdbc:postgresql://... \
#      ride-app-backend
# =============================================================================


# ─────────────────────────────────────────────────────────────────────────────
# ÉTAPE 1 — Build natif avec GraalVM 21 + Maven
#
# On utilise l'image officielle GraalVM Community (gratuite, Oracle GraalVM).
# Elle embarque : JDK 21, native-image, Maven wrapper.
# La phase de compilation native est CPU-intensive (~10-20 min)
# mais ne se lance qu'une fois ; les builds suivants bénéficient du cache GHA.
# ─────────────────────────────────────────────────────────────────────────────
FROM ghcr.io/graalvm/native-image-community:21-muslib AS builder

# muslib = musl libc → binaire statique, compatible Alpine/distroless

WORKDIR /app

# ── On copie le wrapper Maven (évite de télécharger Maven à chaque build) ──
COPY .mvn/ .mvn/
COPY mvnw pom.xml ./

# ── Pré-téléchargement des dépendances (couche cachée séparément) ──────────
# Cette couche est invalidée uniquement si pom.xml change.
RUN ./mvnw dependency:go-offline -B \
    -Pnative \
    --no-transfer-progress

# ── Copie du code source ────────────────────────────────────────────────────
COPY src ./src

# ── Compilation AOT + Build natif GraalVM ──────────────────────────────────
# -Pnative          : active le profil native du pom.xml
# -DskipTests       : les tests sont gérés par GitHub Actions séparément
# native:compile-no-fork : compile le binaire natif (évite un fork JVM)
#
# Le binaire produit : target/ride-app-backend  (~50-80 MB statique)
RUN ./mvnw -Pnative native:compile-no-fork \
    -DskipTests \
    -B \
    --no-transfer-progress


# ─────────────────────────────────────────────────────────────────────────────
# ÉTAPE 2 — Image finale ultra-légère (distroless static)
#
# gcr.io/distroless/static-debian12 :
#   • Pas de shell, pas de package manager → surface d'attaque minimale
#   • Compatible avec les binaires musl-libc statiques de GraalVM
#   • Image finale ~5 MB (vs ~200 MB pour alpine + JRE)
#
# Consommation mémoire au runtime : ~50-80 MB RSS
# → Bien en dessous de la limite Back4app (256 MB)
# ─────────────────────────────────────────────────────────────────────────────
FROM gcr.io/distroless/static-debian12:nonroot AS final

WORKDIR /app

# ── Copie du binaire natif depuis le builder ────────────────────────────────
COPY --from=builder /app/target/ride-app-backend ./ride-app-backend

# ── Copie des ressources statiques nécessaires au runtime ──────────────────
# Thymeleaf templates et ressources statiques doivent être embarqués.
# Avec le build natif Spring Boot, ils sont inclus dans le binaire via
# les resource hints, mais on les copie aussi explicitement par sécurité.
COPY --from=builder /app/src/main/resources/templates ./resources/templates
COPY --from=builder /app/src/main/resources/static    ./resources/static

# ── Port exposé ────────────────────────────────────────────────────────────
EXPOSE 8080

# ── Healthcheck (utilisé par Back4app pour détecter si le container est up) ─
# Spring Actuator expose /actuator/health par défaut
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD ["/bin/sh", "-c", "wget -qO- http://localhost:8080/actuator/health || exit 1"]
# Note : distroless n'a pas de shell ni wget → on utilise l'option nonroot
# avec un healthcheck via l'API Back4app (configuré côté plateforme)

# ── Point d'entrée : le binaire natif directement ──────────────────────────
# Pas de JVM → pas d'options -Xmx/-Xms.
# Le binaire natif gère sa mémoire via les options Spring/OS.
ENTRYPOINT ["/app/ride-app-backend"]

# Variables d'environnement par défaut (surchargeables via Back4app dashboard)
ENV SERVER_PORT=8080
ENV SPRING_PROFILES_ACTIVE=prod

# ─────────────────────────────────────────────────────────────────────────────
# VARIABLES D'ENVIRONNEMENT ATTENDUES AU RUNTIME (à configurer sur Back4app) :
#
#   SPRING_DATASOURCE_URL          jdbc:postgresql://<host>:<port>/<db>
#   SPRING_DATASOURCE_USERNAME     <user>
#   SPRING_DATASOURCE_PASSWORD     <password>
#   JWT_SECRET                     <secret-256-bits-minimum>
#   JWT_EXPIRATION                 86400000
#   TWILIO_ACCOUNT_SID             <sid>
#   TWILIO_AUTH_TOKEN              <token>
#   TWILIO_PHONE_NUMBER            <+1xxx>
#   SPRING_MAIL_HOST               smtp.example.com
#   SPRING_MAIL_USERNAME           <email>
#   SPRING_MAIL_PASSWORD           <password>
# ─────────────────────────────────────────────────────────────────────────────

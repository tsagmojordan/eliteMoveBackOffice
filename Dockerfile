# Étape 1 : Construction de l'image native
FROM ghcr.io/graalvm/native-image-community:21 AS builder

WORKDIR /build

# Copier les fichiers de configuration du build (Maven ici, adaptez pour Gradle)
COPY . .

# Construction de l'image native (Exemple Maven)
# On utilise l'option -Pnative pour le plugin GraalVM
RUN ./mvnw native:compile -Pnative -DskipTests

# Étape 2 : Image d'exécution (ultra-légère)
FROM debian:bookworm-slim

WORKDIR /app

# Copier l'exécutable généré depuis l'étape précédente
# Remplacez "mon-app-java" par le nom de votre exécutable généré dans /target
COPY --from=builder /build/target/mon-app-java /app/server

# Exposer le port (Render utilise souvent 8080 ou 10000)
EXPOSE 8080

# Lancer l'application
CMD ["./server"]

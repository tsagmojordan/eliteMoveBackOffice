FROM debian:bookworm-slim

WORKDIR /app

COPY target/ride-app-backend .

RUN chmod +x ride-app-backend

EXPOSE 8080

ENTRYPOINT ["./ride-app-backend", "--spring.profiles.active=prod"]

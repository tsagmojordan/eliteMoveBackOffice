package com.karibu.ride_app_backend.shared.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payload d'événement publié lorsqu'un utilisateur se connecte avec succès.
 *
 * <p>
 * Publié par : module {@code authentication}.
 * Consommé par : module {@code notification} (alerte de connexion si adresse IP
 * inconnue).
 *
 * @param eventId    Identifiant unique de l'événement.
 * @param occurredOn Horodatage de la connexion.
 * @param userId     Identifiant de l'utilisateur.
 * @param username   Nom d'utilisateur.
 * @param email      E-mail de l'utilisateur.
 * @param ipAddress  Adresse IP source de la connexion.
 * @param userAgent  User-Agent du navigateur/client.
 */
public record UserLoggedInEvent(
        UUID eventId,
        LocalDateTime occurredOn,
        UUID userId,
        String username,
        String email,
        String ipAddress,
        String userAgent) implements Serializable {

    public static UserLoggedInEvent of(
            final UUID userId,
            final String username,
            final String email,
            final String ipAddress,
            final String userAgent) {
        return new UserLoggedInEvent(
                UUID.randomUUID(),
                LocalDateTime.now(),
                userId,
                username,
                email,
                ipAddress,
                userAgent);
    }
}

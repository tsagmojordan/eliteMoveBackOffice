package com.karibu.ride_app_backend.shared.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payload d'événement publié lorsqu'un utilisateur est créé.
 *
 * <p>
 * Publié par : module {@code authentication}.
 * Consommé par : module {@code notification} (envoi e-mail de bienvenue).
 *
 * @param eventId    Identifiant unique de l'événement.
 * @param occurredOn Horodatage de l'événement.
 * @param userId     Identifiant de l'utilisateur créé.
 * @param username   Nom d'utilisateur.
 * @param email      Adresse e-mail de l'utilisateur.
 * @param firstname  Prénom de l'utilisateur.
 * @param lastname   Nom de famille de l'utilisateur.
 */
public record UserCreatedEvent(
        UUID eventId,
        LocalDateTime occurredOn,
        UUID userId,
        String username,
        String email,
        String firstname,
        String lastname) implements Serializable {

    /** Constructeur de commodité — génère automatiquement eventId et occurredOn. */
    public static UserCreatedEvent of(
            final UUID userId,
            final String username,
            final String email,
            final String firstname,
            final String lastname) {
        return new UserCreatedEvent(
                UUID.randomUUID(),
                LocalDateTime.now(),
                userId,
                username,
                email,
                firstname,
                lastname);
    }
}

package com.karibu.ride_app_backend.shared.valueobject;

import java.io.Serializable;
import java.util.UUID;

/**
 * Payload représentant les détails d'un utilisateur.
 */
public record UserPayload(
        UUID id,
        String username,
        String email,
        String role,
        String firstname,
        String lastname,
        String phone
) implements Serializable {

    /** Constructeur canonique — validation des champs essentiels. */
    public UserPayload {
        if (id == null) throw new InvalidUserPayloadException("id must not be null");
        if (username == null || username.isBlank()) throw new InvalidUserPayloadException("username must not be null or blank");
        if (email == null || email.isBlank()) throw new InvalidUserPayloadException("email must not be null or blank");
    }

    /** Exception spécifique au record pour les validations. */
    public static class InvalidUserPayloadException extends IllegalArgumentException {
        public InvalidUserPayloadException(String message) {
            super(message);
        }

        public InvalidUserPayloadException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}


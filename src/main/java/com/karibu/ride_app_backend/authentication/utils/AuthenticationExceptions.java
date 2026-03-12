package com.karibu.ride_app_backend.authentication.utils;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

/**
 * Exceptions métier centralisées pour le module authentication.
 *
 * <p>
 * Chaque classe interne correspond à un cas d'erreur précis,
 * mappé sur un code HTTP approprié.
 */
public final class AuthenticationExceptions {

    private AuthenticationExceptions() {
        // Classe utilitaire — ne pas instancier
    }

    /** Ressource introuvable (404). */
    public static class ResourceNotFoundException extends ResponseStatusException {
        public ResourceNotFoundException(final String message) {
            super(HttpStatus.NOT_FOUND, message);
        }
    }

    /** Conflit de données (409) — ex. : username ou email déjà pris. */
    public static class ResourceAlreadyExistsException extends ResponseStatusException {
        public ResourceAlreadyExistsException(final String message) {
            super(HttpStatus.CONFLICT, message);
        }
    }

    /** Accès non autorisé (401). */
    public static class UnauthorizedException extends ResponseStatusException {
        public UnauthorizedException(final String message) {
            super(HttpStatus.UNAUTHORIZED, message);
        }
    }

    /** Opération interdite (403). */
    public static class ForbiddenException extends ResponseStatusException {
        public ForbiddenException(final String message) {
            super(HttpStatus.FORBIDDEN, message);
        }
    }

    /** Erreur de validation métier (422). */
    public static class BusinessValidationException extends ResponseStatusException {
        public BusinessValidationException(final String message) {
            super(HttpStatus.UNPROCESSABLE_ENTITY, message);
        }
    }
}

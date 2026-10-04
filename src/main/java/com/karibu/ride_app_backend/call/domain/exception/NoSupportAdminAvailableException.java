package com.karibu.ride_app_backend.call.domain.exception;

/**
 * Levée lorsqu'aucun administrateur de support n'est disponible
 * (aucun admin activé, ou tous actuellement en appel).
 */
public class NoSupportAdminAvailableException extends RuntimeException {

    public NoSupportAdminAvailableException(final String message) {
        super(message);
    }
}
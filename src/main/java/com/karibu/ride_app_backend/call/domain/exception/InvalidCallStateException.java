package com.karibu.ride_app_backend.call.domain.exception;

/**
 * Levée lorsqu'une transition d'état interdite est demandée sur un appel.
 */
public class InvalidCallStateException extends RuntimeException {

    public InvalidCallStateException(final String message) {
        super(message);
    }

    public InvalidCallStateException(final String message, final Throwable cause) {
        super(message, cause);
    }
}

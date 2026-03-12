package com.karibu.ride_app_backend.call.domain.exception;

import java.util.UUID;

/**
 * Levée lorsqu'un appel référencé n'existe pas en base.
 */
public class CallNotFoundException extends RuntimeException {

    public CallNotFoundException(final UUID callId) {
        super("Appel introuvable avec l'identifiant : " + callId);
    }
}

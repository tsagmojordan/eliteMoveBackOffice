package com.karibu.ride_app_backend.call.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Requête REST pour terminer un appel (raccrocher).
 *
 * @param callId Identifiant de l'appel à terminer.
 * @param reason Raison de fin optionnelle (ex: "NORMAL", "NETWORK_ERROR").
 */
public record EndCallRequest(

        @NotNull(message = "L'identifiant de l'appel est obligatoire.") UUID callId,

        String reason

) {
}

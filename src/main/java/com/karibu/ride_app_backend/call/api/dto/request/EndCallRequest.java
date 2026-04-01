package com.karibu.ride_app_backend.call.api.dto.request;

/**
 * Requête REST pour terminer un appel (raccrocher).
 *
 * @param callId Identifiant de l'appel à terminer.
 * @param reason Raison de fin optionnelle (ex: "NORMAL", "NETWORK_ERROR").
 */
public record EndCallRequest(

                String reason

) {
}

package com.karibu.ride_app_backend.call.api.dto.response;

import java.util.UUID;

/**
 * Réponse retournée après l'initiation d'un appel.
 *
 * @param callId  Identifiant de l'appel créé.
 * @param message Message descriptif.
 */
public record InitiateCallResponse(UUID callId, String message) {
}

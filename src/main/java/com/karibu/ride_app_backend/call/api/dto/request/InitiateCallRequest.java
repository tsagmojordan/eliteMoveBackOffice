package com.karibu.ride_app_backend.call.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Requête REST pour initier un appel.
 *
 * @param calleeId Identifiant de l'utilisateur à appeler (obligatoire).
 * @param callType Type d'appel : "AUDIO" ou "VIDEO" (par défaut "AUDIO").
 */
public record InitiateCallRequest(

        @NotNull(message = "L'identifiant du destinataire est obligatoire.") UUID calleeId,

        String callType

) {
    /** Valeur par défaut si non renseignée. */
    public String callType() {
        return callType != null ? callType : "AUDIO";
    }
}

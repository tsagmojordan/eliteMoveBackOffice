package com.karibu.ride_app_backend.call.api.dto.request;

import jakarta.validation.constraints.NotNull;

import java.util.UUID;

/**
 * Requête pour envoyer un signal WebRTC (SDP ou ICE Candidate).
 */
public record WebRTCSignalRequest(
        @NotNull(message = "L'identifiant de l'appel est obligatoire.") UUID callId,

        @NotNull(message = "Le payload du signal est obligatoire.") Object signal) {
}

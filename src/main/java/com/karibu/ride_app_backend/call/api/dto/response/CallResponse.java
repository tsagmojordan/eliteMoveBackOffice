package com.karibu.ride_app_backend.call.api.dto.response;

import com.karibu.ride_app_backend.call.domain.model.CallStatus;
import com.karibu.ride_app_backend.call.domain.model.CallType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Réponse REST représentant un appel (liste ou détail).
 *
 * @param id              Identifiant de l'appel.
 * @param callerId        Identifiant de l'appelant.
 * @param calleeId        Identifiant du destinataire.
 * @param callType        Type d'appel (AUDIO / VIDEO).
 * @param status          Statut courant.
 * @param createdAt       Date/heure de création.
 * @param answeredAt      Date/heure de décrochage.
 * @param endedAt         Date/heure de fin.
 * @param durationSeconds Durée en secondes.
 * @param endReason       Raison de fin.
 * @param isActive        {@code true} si actif.
 * @param isTerminated    {@code true} si terminé.
 */
public record CallResponse(
        UUID id,
        UUID callerId,
        UUID calleeId,
        CallType callType,
        CallStatus status,
        LocalDateTime createdAt,
        LocalDateTime answeredAt,
        LocalDateTime endedAt,
        Long durationSeconds,
        String endReason,
        boolean isActive,
        boolean isTerminated) {
}

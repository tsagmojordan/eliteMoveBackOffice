package com.karibu.ride_app_backend.call.application.dto;

import com.karibu.ride_app_backend.call.domain.model.CallStatus;
import com.karibu.ride_app_backend.call.domain.model.CallType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Résumé d'un appel unique retourné lors d'une consultation détaillée.
 *
 * @param id              Identifiant unique de l'appel.
 * @param callerId        Identifiant de l'appelant.
 * @param calleeId        Identifiant du destinataire.
 * @param callType        Type d'appel.
 * @param status          Statut actuel de l'appel.
 * @param createdAt       Date de création.
 * @param answeredAt      Date de décrochage.
 * @param endedAt         Date de fin.
 * @param durationSeconds Durée en secondes.
 * @param endReason       Raison de fin.
 * @param isActive        {@code true} si l'appel est en cours.
 * @param isTerminated    {@code true} si l'appel est terminé.
 */
public record CallSummary(
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

package com.karibu.ride_app_backend.call.application.dto;

import com.karibu.ride_app_backend.call.domain.model.CallStatus;
import com.karibu.ride_app_backend.call.domain.model.CallType;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO interne représentant un élément de l'historique des appels.
 *
 * <p>
 * Utilisé exclusivement dans la couche application pour transporter
 * les données entre use cases et contrôleurs. Ne dépend pas de JPA ou HTTP.
 *
 * @param id              Identifiant de l'appel.
 * @param callerId        Identifiant de l'appelant.
 * @param calleeId        Identifiant du destinataire.
 * @param callType        Type d'appel (AUDIO / VIDEO).
 * @param status          Statut final de l'appel.
 * @param createdAt       Date/heure d'initiation.
 * @param answeredAt      Date/heure de décrochage (null si non répondu).
 * @param endedAt         Date/heure de fin (null si en cours).
 * @param durationSeconds Durée en secondes (null si non abouti).
 * @param endReason       Raison de fin (null si en cours).
 */
public record CallHistoryItem(
        UUID id,
        UUID callerId,
        UUID calleeId,
        CallType callType,
        CallStatus status,
        LocalDateTime createdAt,
        LocalDateTime answeredAt,
        LocalDateTime endedAt,
        Long durationSeconds,
        String endReason) {
}

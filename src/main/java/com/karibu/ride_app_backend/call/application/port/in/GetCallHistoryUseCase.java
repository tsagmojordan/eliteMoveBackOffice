package com.karibu.ride_app_backend.call.application.port.in;

import com.karibu.ride_app_backend.call.application.dto.CallHistoryItem;
import com.karibu.ride_app_backend.call.application.dto.CallSummary;

import java.util.List;
import java.util.UUID;

/**
 * Port d'entrée — Consulter l'historique des appels.
 *
 * <p>
 * Permet à un utilisateur de voir tous ses appels passés,
 * reçus et manqués, triés par date décroissante.
 */
public interface GetCallHistoryUseCase {

    /**
     * Retourne l'historique paginé d'un utilisateur.
     *
     * @param userId Identifiant de l'utilisateur dont on veut l'historique.
     * @param page   Page (0-based).
     * @param size   Nombre d'éléments par page.
     * @return Liste d'éléments d'historique.
     */
    List<CallHistoryItem> handle(UUID userId, int page, int size);

    /**
     * Retourne le résumé d'un appel spécifique.
     *
     * @param callId Identifiant de l'appel.
     * @return Le résumé de l'appel.
     */
    CallSummary findOne(UUID callId);

    /**
     * Retourne le nombre d'appels manqués non consulté de l'utilisateur.
     *
     * @param userId Identifiant de l'utilisateur.
     * @return Nombre d'appels manqués.
     */
    long countMissed(UUID userId);
}

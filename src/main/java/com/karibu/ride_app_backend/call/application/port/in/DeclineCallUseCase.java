package com.karibu.ride_app_backend.call.application.port.in;

import java.util.UUID;

/**
 * Port d'entrée — Décliner un appel entrant.
 *
 * <p>
 * Déclenché lorsque le destinataire appuie sur "Refuser".
 * Passe l'appel de {@code RINGING} à {@code DECLINED} et
 * notifie l'appelant du refus.
 */
public interface DeclineCallUseCase {

    /**
     * @param command Commande de refus.
     */
    void handle(DeclineCallCommand command);

    /**
     * @param callId   Identifiant de l'appel à décliner.
     * @param calleeId Identifiant du destinataire qui refuse.
     */
    record DeclineCallCommand(UUID callId, UUID calleeId) {
    }
}

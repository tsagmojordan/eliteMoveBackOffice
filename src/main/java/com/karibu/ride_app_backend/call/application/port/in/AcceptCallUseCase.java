package com.karibu.ride_app_backend.call.application.port.in;

import java.util.UUID;

/**
 * Port d'entrée — Accepter un appel entrant.
 *
 * <p>
 * Déclenché lorsque le destinataire appuie sur "Décrocher".
 * Passe l'appel de {@code RINGING} à {@code ACCEPTED}.
 */
public interface AcceptCallUseCase {

    /**
     * @param command Commande contenant l'identifiant de l'appel et le demandeur.
     */
    void handle(AcceptCallCommand command);

    /**
     * @param callId   Identifiant de l'appel à accepter.
     * @param calleeId Identifiant du destinataire qui décroche (vérification
     *                 d'autorisation).
     */
    record AcceptCallCommand(UUID callId, UUID calleeId) {
    }
}

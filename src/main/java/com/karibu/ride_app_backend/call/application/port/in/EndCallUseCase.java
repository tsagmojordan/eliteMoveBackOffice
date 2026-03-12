package com.karibu.ride_app_backend.call.application.port.in;

import java.util.UUID;

/**
 * Port d'entrée — Terminer un appel en cours.
 *
 * <p>
 * Peut être déclenché par l'appelant ou le destinataire pour raccrocher.
 * Passe l'appel à l'état {@code ENDED} et notifie l'autre participant.
 */
public interface EndCallUseCase {

    /**
     * @param command Commande de fin d'appel.
     */
    void handle(EndCallCommand command);

    /**
     * @param callId      Identifiant de l'appel à terminer.
     * @param requesterId Identifiant de l'utilisateur qui raccroche.
     * @param reason      Raison de fin (optionnelle, ex: "NORMAL",
     *                    "NETWORK_ERROR").
     */
    record EndCallCommand(UUID callId, UUID requesterId, String reason) {
    }
}

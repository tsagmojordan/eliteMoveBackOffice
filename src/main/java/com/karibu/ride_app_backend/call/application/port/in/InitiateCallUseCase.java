package com.karibu.ride_app_backend.call.application.port.in;

import java.util.UUID;

/**
 * Port d'entrée — Initier un appel audio.
 *
 * <p>
 * L'appelant déclenche ce use case lors d'un tap sur "Appeler".
 * Le système crée l'appel, le persiste, puis envoie la sonnerie
 * sur l'appareil du destinataire.
 */
public interface InitiateCallUseCase {

    /**
     * @param command Commande contenant les identifiants et le type d'appel.
     * @return L'identifiant UUID de l'appel créé.
     */
    UUID handle(InitiateCallCommand command);

    /**
     * Commande immuable portant les données nécessaires à l'initiation.
     *
     * @param callerId Identifiant de l'utilisateur qui appelle.
     * @param calleeId Identifiant de l'utilisateur appelé.
     * @param callType Type d'appel souhaité (ex: "AUDIO").
     */
    record InitiateCallCommand(UUID callerId, UUID calleeId, String callType) {
    }
}

package com.karibu.ride_app_backend.call.domain.model;

/**
 * Représente les états possibles d'un appel audio.
 *
 * <p>
 * Machine à états :
 * 
 * <pre>
 * INITIATED → RINGING → ACCEPTED → IN_PROGRESS → ENDED
 *           ↘ MISSED (timeout)
 *           ↘ DECLINED (refus explicite)
 *           ↘ FAILED (erreur technique)
 * </pre>
 */
public enum CallStatus {

    /**
     * L'appel a été créé côté appelant mais la sonnerie n'a pas encore été envoyée.
     */
    INITIATED,

    /**
     * La sonnerie a été envoyée sur le(s) appareil(s) du destinataire.
     */
    RINGING,

    /**
     * Le destinataire a accepté l'appel. La session WebRTC est en cours
     * d'établissement.
     */
    ACCEPTED,

    /**
     * L'appel est actif. Les deux parties communiquent via WebRTC.
     */
    IN_PROGRESS,

    /**
     * L'appel s'est terminé normalement par l'une ou l'autre des parties.
     */
    ENDED,

    /**
     * Le destinataire n'a pas répondu dans le délai imparti.
     */
    MISSED,

    /**
     * L'appel a été explicitement décliné par le destinataire.
     */
    DECLINED,

    /**
     * L'appel a échoué suite à une erreur technique.
     */
    FAILED
}

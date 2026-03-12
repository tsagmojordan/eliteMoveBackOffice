package com.karibu.ride_app_backend.call.domain.port.out;

import java.util.UUID;

/**
 * Port de sortie — Envoi de signaux temps-réel vers l'application mobile.
 *
 * <p>
 * Permet au domaine de déclencher la sonnerie ou d'envoyer des signaux
 * WebRTC sans connaître le mécanisme de transport (WebSocket, FCM, APNs…).
 *
 * <p>
 * Implémenté dans l'infrastructure par {@code CallSignalingAdapter}.
 */
public interface CallSignalingPort {

    /**
     * Envoie un signal de sonnerie entrante vers le(s) appareil(s) du destinataire.
     *
     * @param callId   Identifiant de l'appel en cours.
     * @param calleeId Identifiant du destinataire à notifier.
     * @param callerId Identifiant de l'appelant (pour afficher le nom / avatar).
     */
    void notifyIncomingCall(UUID callId, UUID calleeId, UUID callerId);

    /**
     * Signale au destinataire que l'appel a été annulé par l'appelant.
     *
     * @param callId   Identifiant de l'appel.
     * @param calleeId Identifiant du destinataire.
     */
    void notifyCallCancelled(UUID callId, UUID calleeId);

    /**
     * Signale à l'appelant que son appel a été décliné.
     *
     * @param callId   Identifiant de l'appel.
     * @param callerId Identifiant de l'appelant.
     */
    void notifyCallDeclined(UUID callId, UUID callerId);

    /**
     * Signale à l'appelant que l'appel a été accepté — WebRTC peut démarrer.
     *
     * @param callId   Identifiant de l'appel.
     * @param callerId Identifiant de l'appelant à notifier.
     */
    void notifyCallAccepted(UUID callId, UUID callerId);

    /**
     * Notifie les deux participants de la fin de l'appel.
     *
     * @param callId   Identifiant de l'appel.
     * @param callerId Identifiant de l'appelant.
     * @param calleeId Identifiant du destinataire.
     */
    void notifyCallEnded(UUID callId, UUID callerId, UUID calleeId);

    /**
     * Transmet un signal de négociation WebRTC (SDP ou ICE Candidate) au
     * destinataire.
     *
     * @param callId        Identifiant de l'appel.
     * @param recipientId   Identifiant de l'utilisateur qui doit recevoir le
     *                      signal.
     * @param signalPayload Objet opaque contenant le signal (SDP offer/answer ou
     *                      ICE).
     */
    void sendWebRTCSignal(UUID callId, UUID recipientId, Object signalPayload);
}

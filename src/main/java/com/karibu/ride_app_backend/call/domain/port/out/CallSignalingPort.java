package com.karibu.ride_app_backend.call.domain.port.out;

import com.karibu.ride_app_backend.call.domain.model.Call;
import com.karibu.ride_app_backend.call.domain.model.CallStatus;

import java.util.UUID;

/**
 * Port de sortie — Envoi des événements d'appel temps réel vers l'application
 * mobile.
 *
 * <p>
 * Permet au domaine de notifier un appel entrant, un changement de statut ou
 * de relayer la signalisation WebRTC sans connaître le mécanisme de transport
 * (WebSocket, FCM, APNs…).
 *
 * <p>
 * Contrat des messages WebSocket (gelé — voir back.md, contrat C7) —
 * destination {@code /user/queue/calls} :
 * <ul>
 * <li>{@code INCOMING_CALL} : {@code {"type":"INCOMING_CALL","call":{...CallResponse...}}}
 * poussé au callee au moment de l'initiation</li>
 * <li>{@code SIGNAL} : {@code {"type":"SIGNAL","callId":...,"signal":{...}}}
 * relayé au participant qui n'est pas l'expéditeur</li>
 * <li>{@code CALL_STATUS} : {@code {"type":"CALL_STATUS","callId":...,"status":"ACCEPTED|DECLINED|ENDED|MISSED"}}
 * poussé aux deux parties</li>
 * </ul>
 *
 * <p>
 * Implémenté dans l'infrastructure par {@code CallSignalingAdapter}.
 */
public interface CallSignalingPort {

    /**
     * Pousse l'événement d'appel entrant vers le destinataire.
     *
     * @param call L'agrégat de l'appel fraîchement créé (sérialisé tel quel
     *             dans le champ {@code call} du message).
     */
    void notifyIncomingCall(Call call);

    /**
     * Pousse un changement de statut d'appel vers les deux parties.
     *
     * @param callId    Identifiant de l'appel.
     * @param status    Nouveau statut (ACCEPTED, DECLINED, ENDED ou MISSED).
     * @param callerId  Identifiant de l'appelant.
     * @param calleeId  Identifiant du destinataire.
     */
    void notifyCallStatus(UUID callId, CallStatus status, UUID callerId, UUID calleeId);

    /**
     * Transmet un signal de négociation WebRTC (SDP offer/answer ou ICE
     * Candidate) au participant qui n'est pas l'expéditeur.
     *
     * @param callId        Identifiant de l'appel.
     * @param recipientId   Identifiant de l'utilisateur qui doit recevoir le
     *                      signal.
     * @param signalPayload Objet opaque contenant le signal, relayé tel quel.
     */
    void sendWebRTCSignal(UUID callId, UUID recipientId, Object signalPayload);
}
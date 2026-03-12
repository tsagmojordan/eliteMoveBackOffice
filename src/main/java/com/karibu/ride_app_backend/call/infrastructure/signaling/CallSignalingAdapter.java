package com.karibu.ride_app_backend.call.infrastructure.signaling;

import com.karibu.ride_app_backend.call.domain.port.out.CallSignalingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Adaptateur de signalisation — Implémente {@link CallSignalingPort} via
 * WebSocket (STOMP).
 *
 * <p>
 * Chaque signal est envoyé sur une destination spécifique de l'utilisateur :
 * {@code /user/{userId}/queue/calls}.
 *
 * <p>
 * Le client mobile écoute ce topic et réagit aux événements de type :
 * <ul>
 * <li>{@code INCOMING_CALL} : Déclenche la sonnerie locale</li>
 * <li>{@code CALL_ACCEPTED} : Démarre la session WebRTC</li>
 * <li>{@code CALL_DECLINED} : Arrête la sonnerie côté appelant</li>
 * <li>{@code CALL_CANCELLED} : Arrête la sonnerie côté destinataire</li>
 * <li>{@code CALL_ENDED} : Ferme la session WebRTC</li>
 * </ul>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallSignalingAdapter implements CallSignalingPort {

    /**
     * Destination WebSocket par utilisateur (user queue pour les signaux d'appel).
     */
    private static final String CALL_QUEUE = "/queue/calls";

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public void notifyIncomingCall(final UUID callId, final UUID calleeId, final UUID callerId) {
        log.info("[CallSignaling] INCOMING_CALL → callee={} (callId={}, caller={})", calleeId, callId, callerId);

        final Map<String, Object> payload = Map.of(
                "type", "INCOMING_CALL",
                "callId", callId.toString(),
                "callerId", callerId.toString());

        sendToUser(calleeId, payload);
    }

    @Override
    public void notifyCallCancelled(final UUID callId, final UUID calleeId) {
        log.info("[CallSignaling] CALL_CANCELLED → callee={} (callId={})", calleeId, callId);

        final Map<String, Object> payload = Map.of(
                "type", "CALL_CANCELLED",
                "callId", callId.toString());

        sendToUser(calleeId, payload);
    }

    @Override
    public void notifyCallDeclined(final UUID callId, final UUID callerId) {
        log.info("[CallSignaling] CALL_DECLINED → caller={} (callId={})", callerId, callId);

        final Map<String, Object> payload = Map.of(
                "type", "CALL_DECLINED",
                "callId", callId.toString());

        sendToUser(callerId, payload);
    }

    @Override
    public void notifyCallAccepted(final UUID callId, final UUID callerId) {
        log.info("[CallSignaling] CALL_ACCEPTED → caller={} (callId={})", callerId, callId);

        final Map<String, Object> payload = Map.of(
                "type", "CALL_ACCEPTED",
                "callId", callId.toString());

        sendToUser(callerId, payload);
    }

    @Override
    public void notifyCallEnded(final UUID callId, final UUID callerId, final UUID calleeId) {
        log.info("[CallSignaling] CALL_ENDED → caller={}, callee={} (callId={})", callerId, calleeId, callId);

        final Map<String, Object> payload = Map.of(
                "type", "CALL_ENDED",
                "callId", callId.toString());

        sendToUser(callerId, payload);
        sendToUser(calleeId, payload);
    }

    @Override
    public void sendWebRTCSignal(final UUID callId, final UUID recipientId, final Object signalPayload) {
        log.debug("[CallSignaling] WEBRTC_SIGNAL → recipient={} (callId={})", recipientId, callId);

        final Map<String, Object> payload = Map.of(
                "type", "WEBRTC_SIGNAL",
                "callId", callId.toString(),
                "signal", signalPayload);

        sendToUser(recipientId, payload);
    }

    // =========================================================================
    // Méthode interne
    // =========================================================================

    /**
     * Envoie un message WebSocket à un utilisateur spécifique via son ID.
     * Utilise la convention Spring STOMP de routing par utilisateur.
     *
     * @param userId  Identifiant de l'utilisateur.
     * @param payload Corps du message.
     */
    private void sendToUser(final UUID userId, final Map<String, Object> payload) {
        try {
            messagingTemplate.convertAndSendToUser(
                    userId.toString(),
                    CALL_QUEUE,
                    payload);
        } catch (Exception ex) {
            log.error("[CallSignaling] Echec d'envoi WebSocket pour userId={}. Cause: {}", userId, ex.getMessage());
            // En production : implémenter un fallback FCM/APNs ici
        }
    }
}

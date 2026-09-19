package com.karibu.ride_app_backend.call.infrastructure.signaling;

import com.karibu.ride_app_backend.call.api.dto.response.CallResponse;
import com.karibu.ride_app_backend.call.domain.model.Call;
import com.karibu.ride_app_backend.call.domain.model.CallStatus;
import com.karibu.ride_app_backend.call.domain.port.out.CallSignalingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

/**
 * Adaptateur de signalisation — Implémente {@link CallSignalingPort} via
 * WebSocket (STOMP), en réutilisant le broker {@code /ws-notifications}.
 *
 * <p>
 * Chaque message est envoyé sur la queue utilisateur du destinataire :
 * {@code /user/queue/calls}. Le routage se fait sur l'UUID de l'utilisateur
 * (le principal WS est nommé par son UUID — voir {@code WebSocketConfig}).
 *
 * <p>
 * Contrat des messages (gelé — voir back.md, contrat C7) :
 * <ul>
 * <li>{@code INCOMING_CALL} : poussé au callee au moment du POST /api/v1/calls,
 * avec l'appel complet dans le champ {@code call}</li>
 * <li>{@code SIGNAL} : relay de la signalisation WebRTC (SDP/ICE), payload
 * intacte dans le champ {@code signal}</li>
 * <li>{@code CALL_STATUS} : poussé aux deux parties sur accept/decline/end
 * (et MISSED sur timeout de sonnerie)</li>
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
    public void notifyIncomingCall(final Call call) {
        log.info("[CallSignaling] INCOMING_CALL → callee={} (callId={}, caller={})",
                call.getCalleeId(), call.getId(), call.getCallerId());

        final Map<String, Object> payload = Map.of(
                "type", "INCOMING_CALL",
                "call", toCallResponse(call));

        sendToUser(call.getCalleeId(), payload);
    }

    @Override
    public void notifyCallStatus(final UUID callId, final CallStatus status,
            final UUID callerId, final UUID calleeId) {
        log.info("[CallSignaling] CALL_STATUS → caller={}, callee={} (callId={}, status={})",
                callerId, calleeId, callId, status);

        final Map<String, Object> payload = Map.of(
                "type", "CALL_STATUS",
                "callId", callId.toString(),
                "status", status.name());

        sendToUser(callerId, payload);
        sendToUser(calleeId, payload);
    }

    @Override
    public void sendWebRTCSignal(final UUID callId, final UUID recipientId, final Object signalPayload) {
        log.debug("[CallSignaling] SIGNAL → recipient={} (callId={})", recipientId, callId);

        final Map<String, Object> payload = Map.of(
                "type", "SIGNAL",
                "callId", callId.toString(),
                "signal", signalPayload);

        sendToUser(recipientId, payload);
    }

    // =========================================================================
    // Méthodes internes
    // =========================================================================

    /**
     * Sérialise un agrégat {@link Call} au format exact du
     * {@link CallResponse} REST (contrat gelé — mêmes noms de champs).
     */
    private CallResponse toCallResponse(final Call call) {
        return new CallResponse(
                call.getId(),
                call.getCallerId(),
                call.getCalleeId(),
                call.getCallType(),
                call.getStatus(),
                call.getCreatedAt(),
                call.getAnsweredAt(),
                call.getEndedAt(),
                call.getDurationSeconds(),
                call.getEndReason(),
                call.isActive(),
                call.isTerminated());
    }

    /**
     * Envoie un message WebSocket à un utilisateur spécifique via son UUID.
     * Utilise la convention Spring STOMP de routing par utilisateur.
     *
     * @param userId  UUID de l'utilisateur.
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
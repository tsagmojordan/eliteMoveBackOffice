package com.karibu.ride_app_backend.call.infrastructure.websocket;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.BinaryMessage;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.BinaryWebSocketHandler;

import java.io.IOException;
import java.net.URI;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Handler WebSocket pour le flux audio brut (Binary Relay).
 *
 * <p>
 * Permet de relayer le son d'un appel entre les deux participants via le
 * serveur
 * sans utiliser de solution tierce P2P (comme Twilio).
 *
 * <p>
 * Url de connexion : ws://host:port/api/v1/calls/{callId}/stream/{userId}
 */
@Slf4j
@Component
public class AudioStreamWebSocketHandler extends BinaryWebSocketHandler {

    // Regex pour extraire callId et userId du chemin de l'URI
    private static final Pattern PATH_PATTERN = Pattern.compile("^/api/v1/calls/([^/]+)/stream/([^/]+)$");

    // Map imbriquée : callId -> (userId -> WebSocketSession)
    private final Map<String, Map<String, WebSocketSession>> activeCalls = new ConcurrentHashMap<>();

    @Override
    public void afterConnectionEstablished(WebSocketSession session) {
        final URI uri = session.getUri();
        if (uri == null) {
            closeSession(session, CloseStatus.BAD_DATA);
            return;
        }

        final Matcher matcher = PATH_PATTERN.matcher(uri.getPath());
        if (!matcher.matches()) {
            log.error("[AudioStream] URI invalide pour le stream: {}", uri.getPath());
            closeSession(session, CloseStatus.BAD_DATA);
            return;
        }

        final String callId = matcher.group(1);
        final String userId = matcher.group(2);

        activeCalls.computeIfAbsent(callId, k -> new ConcurrentHashMap<>()).put(userId, session);
        log.info("[AudioStream] Utilisateur {} connecté au flux de l'appel {}", userId, callId);
    }

    @Override
    protected void handleBinaryMessage(WebSocketSession session, BinaryMessage message) throws Exception {
        final URI uri = session.getUri();
        if (uri == null)
            return;

        final Matcher matcher = PATH_PATTERN.matcher(uri.getPath());
        if (!matcher.matches())
            return;

        final String callId = matcher.group(1);
        final String senderUserId = matcher.group(2);

        final Map<String, WebSocketSession> participants = activeCalls.get(callId);
        if (participants == null || participants.size() < 2) {
            // L'autre participant n'est pas encore connecté
            return;
        }

        // Relayer le message audio aux autres participants du même appel
        for (Map.Entry<String, WebSocketSession> entry : participants.entrySet()) {
            final String participantId = entry.getKey();
            final WebSocketSession targetSession = entry.getValue();

            // Ne pas renvoyer le son à l'expéditeur
            if (!participantId.equals(senderUserId) && targetSession.isOpen()) {
                try {
                    targetSession.sendMessage(message);
                } catch (IOException e) {
                    log.error("[AudioStream] Erreur lors de l'envoi du flux audio", e);
                }
            }
        }
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) {
        final URI uri = session.getUri();
        if (uri == null)
            return;

        final Matcher matcher = PATH_PATTERN.matcher(uri.getPath());
        if (matcher.matches()) {
            final String callId = matcher.group(1);
            final String userId = matcher.group(2);

            final Map<String, WebSocketSession> participants = activeCalls.get(callId);
            if (participants != null) {
                participants.remove(userId);
                log.info("[AudioStream] Utilisateur {} déconnecté du flux de l'appel {}", userId, callId);

                // Si plus de participants, on nettoie le call de la mémoire
                if (participants.isEmpty()) {
                    activeCalls.remove(callId);
                    log.debug("[AudioStream] Nettoyage de l'appel {} (plus de participants)", callId);
                }
            }
        }
    }

    private void closeSession(WebSocketSession session, CloseStatus status) {
        try {
            session.close(status);
        } catch (IOException e) {
            log.error("[AudioStream] Erreur lors de la fermeture de session", e);
        }
    }
}

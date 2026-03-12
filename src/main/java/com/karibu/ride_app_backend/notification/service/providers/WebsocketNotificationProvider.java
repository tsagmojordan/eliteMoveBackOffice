package com.karibu.ride_app_backend.notification.service.providers;

import com.karibu.ride_app_backend.shared.enums.NotificationChannel;
import com.karibu.ride_app_backend.shared.event.NotificationRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

/**
 * Push WebSockets Temps-Réel (via Spring STOMP).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class WebsocketNotificationProvider implements NotificationProvider {

    private final SimpMessagingTemplate messagingTemplate;

    @Override
    public NotificationChannel getSupportedChannel() {
        return NotificationChannel.WEBSOCKET;
    }

    @Override
    public void sendNotification(final NotificationRequestedEvent event) {
        if (event.recipientId() == null) {
            // Pas de destinataire spécifique --> Broadcast global (ex: "Alerte système
            // globale")
            log.info("[WebsocketProvider] Broadcast public d'un événement global: {}", event.subject());
            messagingTemplate.convertAndSend("/topic/public", event);
        } else {
            // Envoyer à l'utilisateur précis : /user/{id}/queue/alerts
            log.info("[WebsocketProvider] Push WebSockets ciblé pour l'utilisateur id={}", event.recipientId());
            messagingTemplate.convertAndSendToUser(
                    event.recipientId().toString(),
                    "/queue/alerts",
                    event);
        }
    }
}

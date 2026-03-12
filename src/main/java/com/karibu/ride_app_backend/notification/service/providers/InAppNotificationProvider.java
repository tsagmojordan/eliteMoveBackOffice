package com.karibu.ride_app_backend.notification.service.providers;

import com.karibu.ride_app_backend.notification.model.InAppNotification;
import com.karibu.ride_app_backend.notification.repository.InAppNotificationRepository;
import com.karibu.ride_app_backend.shared.enums.NotificationChannel;
import com.karibu.ride_app_backend.shared.event.NotificationRequestedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Provider pour la persistance des notifications In-App.
 *
 * <p>
 * Ces notifications s'affichent sous forme de "cloche" dans l'UI du
 * back-office.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InAppNotificationProvider implements NotificationProvider {

    private final InAppNotificationRepository inAppNotificationRepository;

    @Override
    public NotificationChannel getSupportedChannel() {
        return NotificationChannel.IN_APP;
    }

    @Override
    @Transactional
    public void sendNotification(final NotificationRequestedEvent event) {
        if (event.recipientId() == null) {
            log.debug("[InAppNotificationProvider] Sauvegarde ignorée : Aucun ID destinataire");
            return;
        }

        final InAppNotification notification = InAppNotification.builder()
                .recipientId(event.recipientId())
                .subject(event.subject() != null ? event.subject() : "Nouvelle notification")
                .message(buildMessage(event))
                .priority(event.priority() != null ? event.priority().name() : null)
                .templateCode(event.templateCode())
                .read(false)
                .build();

        final InAppNotification saved = inAppNotificationRepository.save(notification);
        log.debug(
                "[InAppNotificationProvider] Notification persistée en base avec succès dans InAppNotification (ID: {})",
                saved.getId());
    }

    private String buildMessage(final NotificationRequestedEvent event) {
        if (event.subject() != null && !event.subject().isBlank()) {
            return event.subject();
        }
        return "Nouvelle notification système";
    }
}

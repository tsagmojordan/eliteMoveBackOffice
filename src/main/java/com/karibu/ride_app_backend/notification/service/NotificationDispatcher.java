package com.karibu.ride_app_backend.notification.service;

import com.karibu.ride_app_backend.notification.service.providers.NotificationProvider;
import com.karibu.ride_app_backend.shared.enums.NotificationChannel;
import com.karibu.ride_app_backend.shared.event.NotificationRequestedEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/**
 * Dispatcher central pour router les requêtes de notification.
 *
 * <p>
 * Charge dynamiquement tous les {@link NotificationProvider} enregistrés
 * et dispatche l'événement aux canaux appropriés (EMAIL, SMS, etc.).
 */
@Slf4j
@Service
public class NotificationDispatcher {

    private final Map<NotificationChannel, NotificationProvider> providersMap;

    public NotificationDispatcher(final List<NotificationProvider> providers) {
        log.debug("[NotificationDispatcher] Initialisation du dispatcher avec {} providers", providers.size());

        providersMap = new EnumMap<>(NotificationChannel.class);
        for (final NotificationProvider provider : providers) {
            final NotificationChannel channel = provider.getSupportedChannel();
            providersMap.put(channel, provider);
            log.debug("[NotificationDispatcher] Enregistrement d'un provider pour le canal: {}", channel);
        }
    }

    /**
     * Dispatche l'événement de notification sur tous les canaux requis.
     *
     * @param event L'événement contenant les destinataires et les canaux à
     *              déclencher.
     */
    public void dispatch(final NotificationRequestedEvent event) {
        if (event.channels() == null || event.channels().isEmpty()) {
            log.debug("[NotificationDispatcher] Aucun canal spécifié. La notification id={} est ignorée",
                    event.eventId());
            return;
        }

        log.debug("[NotificationDispatcher] Dispatching de la notification id={} ({} canal(aux) requis)",
                event.eventId(), event.channels().size());

        for (final NotificationChannel channel : event.channels()) {
            final NotificationProvider provider = providersMap.get(channel);

            if (provider == null) {
                log.debug("[NotificationDispatcher] AVERTISSEMENT : Aucun Provider trouvé pour le canal {}", channel);
                continue;
            }

            try {
                log.debug("[NotificationDispatcher] Délégation au provider pour le canal {}", channel);
                provider.sendNotification(event);
            } catch (Exception ex) {
                log.debug("[NotificationDispatcher] ERREUR : L'envoi via {} a échoué (Erreur: {})", channel,
                        ex.getMessage());
                // En production on utiliserait une dead letter queue ou des retry pattern
            }
        }
    }
}

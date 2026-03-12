package com.karibu.ride_app_backend.notification.service.providers;

import com.karibu.ride_app_backend.shared.enums.NotificationChannel;
import com.karibu.ride_app_backend.shared.event.NotificationRequestedEvent;

/**
 * Interface définissant un fournisseur de notifications.
 *
 * <p>
 * Permet d'appliquer le principe d'Open/Closed (SOLID) pour ajouter facilement
 * de nouveaux canaux
 * (Email, SMS, WebSocket, etc.).
 */
public interface NotificationProvider {

    /**
     * Indique quel canal de notification ce provider prend en charge.
     *
     * @return Le canal géré (EMAIL, SMS, etc.).
     */
    NotificationChannel getSupportedChannel();

    /**
     * Envoie la notification selon la demande.
     *
     * @param event L'événement de demande de notification.
     */
    void sendNotification(NotificationRequestedEvent event);
}

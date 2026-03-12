package com.karibu.ride_app_backend.notification.service.providers;

import com.karibu.ride_app_backend.shared.enums.NotificationChannel;
import com.karibu.ride_app_backend.shared.event.NotificationRequestedEvent;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Provider concret via l'API Twilio - SMS.
 */
@Slf4j
@Service
public class SmsNotificationProvider implements NotificationProvider {

    @Value("${application.twilio.phone-number}")
    private String fromNumber;

    @Override
    public NotificationChannel getSupportedChannel() {
        return NotificationChannel.SMS;
    }

    @Override
    public void sendNotification(final NotificationRequestedEvent event) {
        if (event.recipientPhone() == null || event.recipientPhone().isEmpty()) {
            log.warn("[SmsProvider] Échec: Numéro destinataire manquant pour {}", event.eventId());
            return;
        }

        try {
            // Contruction "crue" du SMS (pas de HTML)
            final String rawBody = String.format(
                    "[%s] %s",
                    event.priority(),
                    event.subject() != null ? event.subject() : "Alerte SmartLighting");

            final Message message = Message.creator(
                    new PhoneNumber(event.recipientPhone()),
                    new PhoneNumber(fromNumber),
                    rawBody).create();

            log.info("[SmsProvider] SMS envoyé avec succès au {}. SID: {}", event.recipientPhone(), message.getSid());
        } catch (Exception e) {
            log.error("[SmsProvider] Twilio API Erreur: {}", e.getMessage());
        }
    }
}

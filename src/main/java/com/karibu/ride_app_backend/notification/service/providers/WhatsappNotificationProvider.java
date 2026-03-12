package com.karibu.ride_app_backend.notification.service.providers;

import com.karibu.ride_app_backend.shared.enums.NotificationChannel;
import com.karibu.ride_app_backend.shared.event.NotificationRequestedEvent;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Provider concret via l'API Twilio - WhatsApp Business.
 */
@Slf4j
@Service
public class WhatsappNotificationProvider implements NotificationProvider {

    @Value("${application.twilio.whatsapp-number}")
    private String fromNumber;

    @Override
    public NotificationChannel getSupportedChannel() {
        return NotificationChannel.WHATSAPP;
    }

    @Override
    public void sendNotification(final NotificationRequestedEvent event) {
        if (event.recipientPhone() == null || event.recipientPhone().isEmpty()) {
            log.warn("[WhatsappProvider] Échec: Numéro destinataire manquant pour {}", event.eventId());
            return;
        }

        try {
            final String rawBody = String.format(
                    "*%s*\n%s",
                    event.subject() != null ? event.subject() : "Notification",
                    event.templateVariables() != null ? event.templateVariables().toString() : "");

            // Syntaxe Twilio: whatsapp:+123456789
            final Message message = Message.creator(
                    new PhoneNumber("whatsapp:" + event.recipientPhone()),
                    new PhoneNumber("whatsapp:" + fromNumber),
                    rawBody).create();

            log.info("[WhatsappProvider] Message WhatsApp envoyé avec succès au {}. SID: {}", event.recipientPhone(),
                    message.getSid());
        } catch (Exception e) {
            log.error("[WhatsappProvider] Twilio API Erreur: {}", e.getMessage());
        }
    }
}

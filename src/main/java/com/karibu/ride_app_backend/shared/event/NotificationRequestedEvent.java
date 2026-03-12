package com.karibu.ride_app_backend.shared.event;

import com.karibu.ride_app_backend.shared.enums.NotificationChannel;
import com.karibu.ride_app_backend.shared.enums.NotificationPriority;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 * Payload d'événement publié pour demander l'envoi d'une notification.
 *
 * <p>
 * Publié par : n'importe quel module applicatif.
 * Consommé par : module {@code notification}.
 *
 * <p>
 * Le champ {@code templateVariables} permet d'injecter des valeurs
 * dans les templates Thymeleaf ou dans les corps de messages.
 *
 * @param eventId           Identifiant unique de l'événement.
 * @param occurredOn        Horodatage de l'événement.
 * @param recipientId       Identifiant du destinataire (peut être null si
 *                          externe).
 * @param recipientEmail    Adresse e-mail du destinataire.
 * @param recipientPhone    Numéro de téléphone (pour SMS / WhatsApp).
 * @param recipientName     Nom affiché du destinataire.
 * @param channels          Canaux d'envoi souhaités (EMAIL, SMS, IN_APP,
 *                          WEBSOCKET, WHATSAPP).
 * @param priority          Priorité de la notification (LOW, NORMAL, HIGH,
 *                          CRITICAL).
 * @param templateCode      Code du template de message (ex. : WELCOME_USER,
 *                          ALERT_OUTAGE).
 * @param templateVariables Variables à injecter dans le template.
 * @param subject           Sujet (utilisé pour EMAIL uniquement).
 */
public record NotificationRequestedEvent(
        UUID eventId,
        LocalDateTime occurredOn,
        UUID recipientId,
        String recipientEmail,
        String recipientPhone,
        String recipientName,
        Set<NotificationChannel> channels,
        NotificationPriority priority,
        String templateCode,
        Map<String, Object> templateVariables,
        String subject) implements Serializable {

    /** Constructeur de commodité — génère automatiquement eventId et occurredOn. */
    public static NotificationRequestedEvent of(
            final UUID recipientId,
            final String recipientEmail,
            final String recipientPhone,
            final String recipientName,
            final Set<NotificationChannel> channels,
            final NotificationPriority priority,
            final String templateCode,
            final Map<String, Object> templateVariables,
            final String subject) {
        return new NotificationRequestedEvent(
                UUID.randomUUID(),
                LocalDateTime.now(),
                recipientId,
                recipientEmail,
                recipientPhone,
                recipientName,
                channels,
                priority,
                templateCode,
                templateVariables,
                subject);
    }
}

package com.karibu.ride_app_backend.shared.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payload d'événement publié lorsqu'une alarme système est déclenchée.
 *
 * <p>
 * Publié par : n'importe quel module de supervision.
 * Consommé par : module {@code notification} (envoi CRITICAL multi-canal).
 *
 * @param eventId        Identifiant unique de l'événement.
 * @param occurredOn     Horodatage.
 * @param alarmId        Identifiant de l'alarme.
 * @param alarmCode      Code de l'alarme (ex. : POWER_OUTAGE, LAMP_FAILURE).
 * @param severity       Sévérité : INFO, WARNING, CRITICAL.
 * @param source         Source de l'alarme (nom du composant/service).
 * @param description    Description humaine de l'alarme.
 * @param affectedZoneId Zone affectée (nullable).
 */
public record AlarmTriggeredEvent(
        UUID eventId,
        LocalDateTime occurredOn,
        UUID alarmId,
        String alarmCode,
        String severity,
        String source,
        String description,
        UUID affectedZoneId) implements Serializable {

    public static AlarmTriggeredEvent of(
            final UUID alarmId,
            final String alarmCode,
            final String severity,
            final String source,
            final String description,
            final UUID affectedZoneId) {
        return new AlarmTriggeredEvent(
                UUID.randomUUID(),
                LocalDateTime.now(),
                alarmId,
                alarmCode,
                severity,
                source,
                description,
                affectedZoneId);
    }
}

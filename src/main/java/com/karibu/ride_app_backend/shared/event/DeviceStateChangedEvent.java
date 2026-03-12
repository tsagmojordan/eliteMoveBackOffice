package com.karibu.ride_app_backend.shared.event;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Payload d'événement publié lorsqu'un équipement d'éclairage change d'état.
 *
 * <p>
 * Publié par : module {@code lighting} (à créer).
 * Consommé par : module {@code notification} (alerte temps réel en
 * WebSocket/in-app).
 *
 * @param eventId       Identifiant unique de l'événement.
 * @param occurredOn    Horodatage.
 * @param deviceId      Identifiant de l'équipement.
 * @param deviceName    Nom de l'équipement.
 * @param zoneId        Zone géographique concernée.
 * @param previousState État précédent (ON, OFF, FAULT, MAINTENANCE).
 * @param newState      Nouvel état.
 * @param triggeredBy   Identifiant de l'utilisateur ayant déclenché le
 *                      changement (nullable).
 */
public record DeviceStateChangedEvent(
        UUID eventId,
        LocalDateTime occurredOn,
        UUID deviceId,
        String deviceName,
        UUID zoneId,
        String previousState,
        String newState,
        UUID triggeredBy) implements Serializable {

    public static DeviceStateChangedEvent of(
            final UUID deviceId,
            final String deviceName,
            final UUID zoneId,
            final String previousState,
            final String newState,
            final UUID triggeredBy) {
        return new DeviceStateChangedEvent(
                UUID.randomUUID(),
                LocalDateTime.now(),
                deviceId,
                deviceName,
                zoneId,
                previousState,
                newState,
                triggeredBy);
    }
}

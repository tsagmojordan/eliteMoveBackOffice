package com.karibu.ride_app_backend.notification.event.listeners;

import com.karibu.ride_app_backend.notification.service.NotificationDispatcher;
import com.karibu.ride_app_backend.shared.enums.NotificationChannel;
import com.karibu.ride_app_backend.shared.enums.NotificationPriority;
import com.karibu.ride_app_backend.shared.event.AlarmTriggeredEvent;
import com.karibu.ride_app_backend.shared.event.DeviceStateChangedEvent;
import com.karibu.ride_app_backend.shared.event.NotificationRequestedEvent;
import com.karibu.ride_app_backend.shared.event.UserCreatedEvent;
import com.karibu.ride_app_backend.shared.event.UserLoggedInEvent;
// ...existing imports...
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Set;

/**
 * Listener principal du module notification.
 *
 * <p>
 * Isole la logique métier d'écoute d'interception d'événements. S'appuie sur
 * {@link ApplicationModuleListener} de Spring Modulith pour un traitement
 * asynchrone automatique.
 *
 * <p>
 * Toutes les écoutes déclenchent des méthodes du dispatcher de notifications.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationEventListener {

    private final NotificationDispatcher dispatcher;

    /**
     * Capture une requête de notification générique.
     */
    @ApplicationModuleListener
    public void onNotificationRequested(final NotificationRequestedEvent event) {
        log.debug("[NotificationEventListener] Récéption asynchrone d'un NotificationRequestedEvent (id={})",
                event.eventId());
        dispatcher.dispatch(event);
    }

    /**
     * Intercepte la création d'un utilisateur et déclenche un e-mail de bienvenue.
     */
    @ApplicationModuleListener
    public void onUserCreated(final UserCreatedEvent event) {
        log.debug("[NotificationEventListener] L'utilisateur (id={}) a été créé. Envoi du mail de bienvenue auto...",
                event.userId());

        final NotificationRequestedEvent emailEvent = NotificationRequestedEvent.of(
                event.userId(),
                event.email(),
                null,
                event.firstname() + " " + event.lastname(),
                Set.of(NotificationChannel.EMAIL, NotificationChannel.IN_APP, NotificationChannel.WEBSOCKET),
                NotificationPriority.NORMAL,
                "WELCOME_EMAIL",
                Map.of("username", event.username()),
                "Bienvenue sur SmartLighting ! \uD83C\uDF1F" // Émoji étoile
        );

        dispatcher.dispatch(emailEvent);
    }

    /**
     * Intercepte les connexions d'un utilisateur. Envoi en temps réel.
     */
    @ApplicationModuleListener
    public void onUserLoggedIn(final UserLoggedInEvent event) {
        log.debug("[NotificationEventListener] Connexion réussie pour id={}. Audit notifié.", event.userId());

        final NotificationRequestedEvent notifEvent = NotificationRequestedEvent.of(
                event.userId(),
                event.email(),
                null,
                event.username(),
                Set.of(NotificationChannel.IN_APP, NotificationChannel.WEBSOCKET, NotificationChannel.EMAIL),
                NotificationPriority.LOW,
                "NEW_LOGIN",
                Map.of("ip", event.ipAddress(), "agent", event.userAgent()),
                "Nouvelle connexion détectée");

        dispatcher.dispatch(notifEvent);
    }

    /**
     * Notifie les clients WebSocket en cas de modification d'état de lampadaire ou
     * zone.
     */
    @ApplicationModuleListener
    public void onDeviceStateChanged(final DeviceStateChangedEvent event) {
        log.debug("[NotificationEventListener] Changement d'état d'appareil id={} détecté !", event.deviceId());

        final NotificationRequestedEvent notifEvent = NotificationRequestedEvent.of(
                null, // Avertissement broadcasté
                null,
                null,
                "System Broadcast",
                Set.of(NotificationChannel.WEBSOCKET),
                "FAULT".equals(event.newState()) ? NotificationPriority.HIGH : NotificationPriority.NORMAL,
                "DEVICE_STATE_CHANGE",
                Map.of(
                        "deviceId", event.deviceId().toString(),
                        "state", event.newState()),
                "Mise à jour d'un appareil - " + event.deviceName());

        dispatcher.dispatch(notifEvent);
    }

    /**
     * Déclenche une notification par SMS ou Mail si une ALARME PANIQUE survient.
     */
    @ApplicationModuleListener
    public void onAlarmTriggered(final AlarmTriggeredEvent event) {
        log.debug("[NotificationEventListener] \u26A0\uFE0F ALARME CRITIQUE déclenchée - id={}", event.alarmId());

        final NotificationRequestedEvent notifEvent = NotificationRequestedEvent.of(
                null, // Destinataires récupérés via une gestion de contacts plus fine en production
                      // (administrateurs)
                "admin@smartlighting.cm", // Par défaut on envoi à l'admin global
                "+23700000000",
                "Superviseurs",
                Set.of(NotificationChannel.EMAIL, NotificationChannel.SMS, NotificationChannel.WEBSOCKET,
                        NotificationChannel.IN_APP),
                NotificationPriority.CRITICAL,
                "ALARM_CRITICAL",
                Map.of("code", event.alarmCode(), "desc", event.description()),
                "URGENT: Problème critique sur le module : " + event.source());

        dispatcher.dispatch(notifEvent);
    }

    // Ride events are handled in a dedicated listener: RideEventListener
}

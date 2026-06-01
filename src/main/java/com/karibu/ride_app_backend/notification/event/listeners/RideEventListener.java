package com.karibu.ride_app_backend.notification.event.listeners;

import com.karibu.ride_app_backend.notification.service.FetchUserDetailsService;
import com.karibu.ride_app_backend.notification.service.NotificationDispatcher;
import com.karibu.ride_app_backend.shared.event.NotificationRequestedEvent;
import com.karibu.ride_app_backend.shared.enums.NotificationChannel;
import com.karibu.ride_app_backend.shared.enums.NotificationPriority;
import com.karibu.ride_app_backend.shared.valueobject.RideCreatedEvent;
import com.karibu.ride_app_backend.shared.valueobject.UserPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Listener dédié aux événements liés aux rides.
 * Gère les notifications pour le requester et les admins lors de la création d'un ride.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RideEventListener {

    private final NotificationDispatcher dispatcher;
    private final FetchUserDetailsService fetchUserDetailsService;

    /**
     * @param event évènement de création d'une nouvelle requête de course
     * Capture la création d'un ride et notifie le requester et les admins.
     */
    @ApplicationModuleListener
    public void onRideCreated(final RideCreatedEvent event) {
        log.debug("[RideEventListener] Ride créé (rideId={}) — préparation des notifications requester et admins", event.rideId());

        // 1. Notifier le demandeur (requester)
        notifyRequester(event);

        // 2. Notifier les administrateurs
        notifyAdmins(event);
    }

    /**
     * Envoie une notification au demandeur du ride avec ses détails récupérés.
     */
    private void notifyRequester(final RideCreatedEvent event) {
        // Si requesterId est fourni, essayons de récupérer les détails utilisateur via le service.
        if (event.requesterId() != null && !event.requesterId().isBlank()) {
            UUID requesterUuid = null;
            try {
                requesterUuid = UUID.fromString(event.requesterId());
            } catch (IllegalArgumentException ex) {
                log.warn("[RideEventListener] requesterId non-UUID='{}'. Envoi sans user details.", event.requesterId());
            }

            if (requesterUuid != null) {
                final CompletableFuture<UserPayload> fut = fetchUserDetailsService.fetchById(requesterUuid);
                UUID finalRequesterUuid = requesterUuid;
                fut.whenComplete((userPayload, ex) -> {
                    if (ex != null) {
                        log.warn("[RideEventListener] Échec fetch requester details pour id={} : {}", finalRequesterUuid, ex.getMessage());
                        // fallback: envoyer notification sans recipientId/email
                        final NotificationRequestedEvent notifEvent = NotificationRequestedEvent.of(
                                null,
                                null,
                                null,
                                "Demandeur de trajet",
                                Set.of(NotificationChannel.IN_APP, NotificationChannel.WEBSOCKET),
                                NotificationPriority.NORMAL,
                                "RIDE_CREATED",
                                Map.of("rideId", event.rideId(), "rideStatus", event.rideStatus(), "vehiculeId", event.vehiculeId()),
                                "Votre véhicule se mettra en route une fois l'approbation de l'admin faite"
                        );
                        dispatcher.dispatch(notifEvent);
                        return;
                    }

                    // Construire la notification en utilisant les infos retournées si disponibles
                    final NotificationRequestedEvent notifEventWithUser = NotificationRequestedEvent.of(
                            userPayload != null ? userPayload.id() : null,
                            userPayload != null ? userPayload.email() : null,
                            userPayload != null ? userPayload.phone() : null,
                            userPayload != null ? (userPayload.firstname() + " " + userPayload.lastname()) : "Demandeur de trajet",
                            Set.of(NotificationChannel.IN_APP, NotificationChannel.WEBSOCKET),
                            NotificationPriority.NORMAL,
                            "RIDE_CREATED",
                            Map.of("rideId", event.rideId(), "rideStatus", event.rideStatus(), "vehiculeId", event.vehiculeId()),
                            "Votre trajet a été créé"
                    );

                    dispatcher.dispatch(notifEventWithUser);
                });
                return; // dispatch done asynchronously
            }
        }

        // Fallback : pas de requesterId ou non-parsable -> envoi sans détails utilisateur
        final NotificationRequestedEvent notifEvent = NotificationRequestedEvent.of(
                null, // pas de recipientId
                null, // pas d'email
                null, // pas de téléphone
                "Demandeur de trajet",
                Set.of(NotificationChannel.IN_APP, NotificationChannel.WEBSOCKET),
                NotificationPriority.NORMAL,
                "RIDE_CREATED",
                Map.of(
                        "rideId", event.rideId(),
                        "rideStatus", event.rideStatus(),
                        "vehiculeId", event.vehiculeId()
                ),
                "Votre trajet a été créé"
        );

        dispatcher.dispatch(notifEvent);
    }

    /**
     * Envoie des notifications à tous les administrateurs avec leurs infos de contact.
     */
    private void notifyAdmins(final RideCreatedEvent event) {
        final CompletableFuture<List<UserPayload>> adminsFuture = fetchUserDetailsService.fetchByRole("ADMIN");
        adminsFuture.whenComplete((admins, ex) -> {
            if (ex != null) {
                log.warn("[RideEventListener] Échec fetch des admins : {}", ex.getMessage());
                return; // Pas de notification si fetch échoue
            }

            if (admins == null || admins.isEmpty()) {
                log.debug("[RideEventListener] Aucun admin trouvé pour notifier");
                return;
            }

            log.debug("[RideEventListener] {} admin(s) trouvé(s). Envoi des notifications...", admins.size());

            // Pour chaque admin, construire et dispatcher une notification avec ses détails de contact
            for (final UserPayload admin : admins) {
                final NotificationRequestedEvent adminNotifEvent = NotificationRequestedEvent.of(
                        admin.id(),
                        admin.email(),
                        admin.phone(),
                        admin.firstname() + " " + admin.lastname(),
                        Set.of(NotificationChannel.EMAIL, NotificationChannel.IN_APP, NotificationChannel.WEBSOCKET),
                        NotificationPriority.HIGH,  // Priorité HAUTE pour les admins
                        "RIDE_CREATED_ADMIN_ALERT",
                        Map.of(
                                "rideId", event.rideId(),
                                "rideStatus", event.rideStatus(),
                                "vehiculeId", event.vehiculeId(),
                                "requesterId", event.requesterId()
                        ),
                        "Une nouvelle demande de véhicule vient d'être émise "
                );

                dispatcher.dispatch(adminNotifEvent);
                log.debug("[RideEventListener] Notification envoyée à l'admin {}", admin.email());
            }
        });
    }
}

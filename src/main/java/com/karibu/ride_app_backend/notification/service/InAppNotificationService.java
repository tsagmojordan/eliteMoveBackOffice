package com.karibu.ride_app_backend.notification.service;

import com.karibu.ride_app_backend.notification.dto.response.InAppNotificationResponse;

import java.util.UUID;

/**
 * Interface de gestion des notifications "In-app" par l'utilisateur final.
 */
public interface InAppNotificationService {

        /**
         * Récupère tout l'historique des notifications pour un utilisateur donné de
         * façon paginée et filtrée.
         */
        com.karibu.ride_app_backend.shared.dto.PaginatedResponse<InAppNotificationResponse> getMyNotifications(
                        UUID userId,
                        String search, org.springframework.data.domain.Pageable pageable);

        /**
         * Récupère uniquement les notifications non-lues pour ce compte de façon
         * paginée et filtrée.
         */
        com.karibu.ride_app_backend.shared.dto.PaginatedResponse<InAppNotificationResponse> getMyUnreadNotifications(
                        UUID userId, String search, org.springframework.data.domain.Pageable pageable);

        /**
         * Compte combien de notifications ne sont pas encore lues. Utile pour un badge
         * (cloche).
         *
         * @param userId Identifiant de l'utilisateur
         * @return Nombre de non-lues.
         */
        long countMyUnreadNotifications(UUID userId);

        /**
         * Marque une notification spécifique comme "lue".
         *
         * @param notificationId ID de la notification
         * @param userId         ID du destinataire pour éviter qu'il marque celles d'un
         *                       autre
         */
        void markAsRead(UUID notificationId, UUID userId);

        /**
         * Marque absolument toutes les notifications de l'utilisateur comme lues d'un
         * coup.
         *
         * @param userId Identifiant du compte.
         */
        void markAllAsRead(UUID userId);
}

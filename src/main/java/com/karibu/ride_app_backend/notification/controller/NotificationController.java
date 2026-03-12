package com.karibu.ride_app_backend.notification.controller;

import com.karibu.ride_app_backend.authentication.model.User;
import com.karibu.ride_app_backend.authentication.utils.ApiResponse;
import com.karibu.ride_app_backend.notification.dto.response.InAppNotificationResponse;
import com.karibu.ride_app_backend.notification.service.InAppNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Endpoint REST permettant au client Web (Angular/React) de
 * consommer l'historique de ses alertes In-App.
 * Accessible pour chaque utilisateur connecté afin de consulter SA propre
 * "Cloche d'Alerte".
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/notifications/in-app")
@RequiredArgsConstructor
public class NotificationController {

        private final InAppNotificationService notificationService;

        /**
         * Voir tout son flux d'alertes.
         * Exemple Endpoint: GET /api/v1/notifications/in-app
         */
        @GetMapping
        public ResponseEntity<ApiResponse<com.karibu.ride_app_backend.shared.dto.PaginatedResponse<InAppNotificationResponse>>> getMyNotifications(
                        @AuthenticationPrincipal final User currentUser,
                        @RequestParam(required = false) final String search,
                        final org.springframework.data.domain.Pageable pageable) {
                log.debug("[NotificationController] GET Historique Complet des notifications");
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Toutes vos notifications",
                                                notificationService.getMyNotifications(currentUser.getId(), search,
                                                                pageable)));
        }

        /**
         * Voir que ses nouvelles alertes (non acquitées).
         * Exemple Endpoint: GET /api/v1/notifications/in-app/unread
         */
        @GetMapping("/unread")
        public ResponseEntity<ApiResponse<com.karibu.ride_app_backend.shared.dto.PaginatedResponse<InAppNotificationResponse>>> getMyUnreadNotifications(
                        @AuthenticationPrincipal final User currentUser,
                        @RequestParam(required = false) final String search,
                        final org.springframework.data.domain.Pageable pageable) {
                log.debug("[NotificationController] GET Notifications Unread");
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Notifications non-lues",
                                                notificationService.getMyUnreadNotifications(currentUser.getId(),
                                                                search, pageable)));
        }

        /**
         * Obtenir juste le décompte (le chiffre "3" ou "5" sur la petite cloche rouge).
         * Exemple Endpoint: GET /api/v1/notifications/in-app/unread/count
         */
        @GetMapping("/unread/count")
        public ResponseEntity<ApiResponse<Long>> countMyUnreadNotifications(
                        @AuthenticationPrincipal final User currentUser) {
                log.debug("[NotificationController] GET Count Unread");
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Nombre d'alertes",
                                                notificationService.countMyUnreadNotifications(currentUser.getId())));
        }

        /**
         * Marquer une notification comme "Lue".
         * L'action fait disparaitre l'UI de sa pile /unread mais la laisse en
         * historique.
         */
        @PatchMapping("/{notificationId}/read")
        public ResponseEntity<ApiResponse<Void>> markAsRead(
                        @PathVariable final UUID notificationId,
                        @AuthenticationPrincipal final User currentUser) {
                log.debug("[NotificationController] PATCH markAsRead sur id={}", notificationId);
                notificationService.markAsRead(notificationId, currentUser.getId());
                return ResponseEntity.ok(ApiResponse.success("Notification marquée comme lue."));
        }

        /**
         * Marquer tout comme lu (bouton "Clear All Notifications").
         */
        @PatchMapping("/read-all")
        public ResponseEntity<ApiResponse<Void>> markAllAsRead(
                        @AuthenticationPrincipal final User currentUser) {
                log.debug("[NotificationController] PATCH markAllAsRead");
                notificationService.markAllAsRead(currentUser.getId());
                return ResponseEntity.ok(ApiResponse.success("Toutes les notifications marquées comme lues."));
        }
}

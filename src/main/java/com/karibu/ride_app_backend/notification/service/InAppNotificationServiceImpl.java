package com.karibu.ride_app_backend.notification.service;

import com.karibu.ride_app_backend.notification.dto.response.InAppNotificationResponse;
import com.karibu.ride_app_backend.notification.mapper.InAppNotificationMapper;
import com.karibu.ride_app_backend.notification.model.InAppNotification;
import com.karibu.ride_app_backend.notification.repository.InAppNotificationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

/**
 * Service concret gérant le stockage et la lecture des InAppNotifications.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InAppNotificationServiceImpl implements InAppNotificationService {

    private final InAppNotificationRepository repository;
    private final InAppNotificationMapper mapper;

    @Override
    public com.karibu.ride_app_backend.shared.dto.PaginatedResponse<InAppNotificationResponse> getMyNotifications(
            final UUID userId, String search, org.springframework.data.domain.Pageable pageable) {
        log.debug("[InAppNotificationService] Récupération de tout l'historique pour user_id={}", userId);
        org.springframework.data.domain.Page<InAppNotification> pageResult = repository.searchMyNotifications(userId,
                search, pageable);
        return com.karibu.ride_app_backend.shared.dto.PaginatedResponse.from(pageResult.map(mapper::toResponse));
    }

    @Override
    public com.karibu.ride_app_backend.shared.dto.PaginatedResponse<InAppNotificationResponse> getMyUnreadNotifications(
            final UUID userId, String search, org.springframework.data.domain.Pageable pageable) {
        log.debug("[InAppNotificationService] Récupération des nouveautés pour user_id={}", userId);
        org.springframework.data.domain.Page<InAppNotification> pageResult = repository
                .searchMyUnreadNotifications(userId, search, pageable);
        return com.karibu.ride_app_backend.shared.dto.PaginatedResponse.from(pageResult.map(mapper::toResponse));
    }

    @Override
    public long countMyUnreadNotifications(final UUID userId) {
        log.debug("[InAppNotificationService] Comptage non-lu pour user_id={}", userId);
        return repository.countByRecipientIdAndReadFalse(userId);
    }

    @Override
    @Transactional
    public void markAsRead(final UUID notificationId, final UUID userId) {
        log.debug("[InAppNotificationService] Acquittement de id={} par user_id={}", notificationId, userId);

        final InAppNotification notif = repository.findById(notificationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                        "Notification introuvable avec l'id : " + notificationId));

        // Mesure de sécurité: on vérifie que l'utilisateur lit une de SES notifications
        if (!notif.getRecipientId().equals(userId)) {
            log.warn(
                    "[InAppNotificationService] Accès Refusé : Utilisateur id={} essaie de marquer l'alerte d'un autre id={}",
                    userId, notificationId);
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Cette notification ne vous appartient pas.");
        }

        notif.setRead(true);
        repository.save(notif);
        log.debug("[InAppNotificationService] Alerte id={} passée en LUE !", notificationId);
    }

    @Override
    @Transactional
    public void markAllAsRead(final UUID userId) {
        log.debug("[InAppNotificationService] Tout marquer comme lu pour user_id={}", userId);
        final List<InAppNotification> unread = repository.findByRecipientIdAndReadFalseOrderByCreatedAtDesc(userId);

        unread.forEach(notif -> notif.setRead(true));
        repository.saveAll(unread);
    }
}

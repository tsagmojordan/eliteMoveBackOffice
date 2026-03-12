package com.karibu.ride_app_backend.notification.repository;

import com.karibu.ride_app_backend.notification.model.InAppNotification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

/**
 * Repository pour gérer l'accès aux données des notifications In-App.
 */
@Repository
public interface InAppNotificationRepository extends JpaRepository<InAppNotification, UUID> {

    /**
     * Récupère toutes les notifications d'un utilisateur, en commençant par les
     * plus récentes.
     */
    List<InAppNotification> findByRecipientIdOrderByCreatedAtDesc(UUID recipientId);

    /**
     * Récupère toutes les notifications non lues d'un utilisateur.
     */
    List<InAppNotification> findByRecipientIdAndReadFalseOrderByCreatedAtDesc(UUID recipientId);

    /**
     * Compte le nombre de notifications non lues pour un utilisateur.
     */
    long countByRecipientIdAndReadFalse(UUID recipientId);

    @org.springframework.data.jpa.repository.Query("SELECT n FROM InAppNotification n WHERE n.recipientId = :recipientId AND "
            +
            "(:search IS NULL OR " +
            "LOWER(n.subject) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(n.message) LIKE LOWER(CONCAT('%', :search, '%')))")
    org.springframework.data.domain.Page<InAppNotification> searchMyNotifications(
            @org.springframework.data.repository.query.Param("recipientId") UUID recipientId,
            @org.springframework.data.repository.query.Param("search") String search,
            org.springframework.data.domain.Pageable pageable);

    @org.springframework.data.jpa.repository.Query("SELECT n FROM InAppNotification n WHERE n.recipientId = :recipientId AND n.read = false AND "
            +
            "(:search IS NULL OR " +
            "LOWER(n.subject) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(n.message) LIKE LOWER(CONCAT('%', :search, '%')))")
    org.springframework.data.domain.Page<InAppNotification> searchMyUnreadNotifications(
            @org.springframework.data.repository.query.Param("recipientId") UUID recipientId,
            @org.springframework.data.repository.query.Param("search") String search,
            org.springframework.data.domain.Pageable pageable);
}

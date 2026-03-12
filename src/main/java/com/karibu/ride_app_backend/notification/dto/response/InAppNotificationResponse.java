package com.karibu.ride_app_backend.notification.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO pour renvoyer une notification In-App au client.
 */
public record InAppNotificationResponse(
        UUID id,
        String subject,
        String message,
        boolean read,
        String priority,
        String templateCode,
        LocalDateTime createdAt) {
}

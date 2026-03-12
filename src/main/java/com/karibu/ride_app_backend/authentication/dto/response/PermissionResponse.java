package com.karibu.ride_app_backend.authentication.dto.response;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * DTO de réponse pour une permission.
 *
 * @param id          Identifiant unique.
 * @param name        Nom technique.
 * @param description Description humaine.
 * @param module      Module applicatif.
 * @param createdAt   Date de création.
 * @param updatedAt   Date de dernière mise à jour.
 */
public record PermissionResponse(
        UUID id,
        String name,
        String description,
        String module,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

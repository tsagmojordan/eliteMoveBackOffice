package com.karibu.ride_app_backend.authentication.dto.response;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO de réponse pour un rôle.
 *
 * @param id          Identifiant unique.
 * @param name        Nom technique.
 * @param description Description humaine.
 * @param permissions Permissions attachées au rôle.
 * @param createdAt   Date de création.
 * @param updatedAt   Date de dernière mise à jour.
 */
public record RoleResponse(
        UUID id,
        String name,
        String description,
        Set<PermissionResponse> permissions,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

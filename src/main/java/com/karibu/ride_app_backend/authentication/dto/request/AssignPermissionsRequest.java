package com.karibu.ride_app_backend.authentication.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;
import java.util.UUID;

/**
 * DTO d'assignation de permissions à un rôle.
 *
 * @param permissionIds Ensemble des UUID de permissions à assigner.
 */
public record AssignPermissionsRequest(

        @NotEmpty(message = "Au moins une permission est requise") Set<UUID> permissionIds) {
}

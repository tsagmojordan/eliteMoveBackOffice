package com.karibu.ride_app_backend.authentication.dto.request;

import jakarta.validation.constraints.NotEmpty;

import java.util.Set;
import java.util.UUID;

/**
 * DTO d'assignation de rôles à un utilisateur.
 *
 * @param roleIds Ensemble des UUID de rôles à assigner.
 */
public record AssignRolesRequest(

        @NotEmpty(message = "Au moins un rôle est requis") Set<UUID> roleIds) {
}

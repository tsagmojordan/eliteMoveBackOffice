package com.karibu.ride_app_backend.authentication.dto.response;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

/**
 * DTO de réponse pour un utilisateur.
 *
 * @param id        Identifiant unique.
 * @param firstname Prénom.
 * @param lastname  Nom de famille.
 * @param username  Identifiant de connexion.
 * @param email     Adresse e-mail.
 * @param enabled   Compte actif ?
 * @param roles     Rôles assignés.
 * @param createdAt Date de création.
 * @param updatedAt Date de dernière mise à jour.
 */
public record UserResponse(
        UUID id,
        String firstname,
        String lastname,
        String username,
        String email,
        boolean enabled,
        Set<RoleResponse> roles,
        LocalDateTime createdAt,
        LocalDateTime updatedAt) {
}

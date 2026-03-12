package com.karibu.ride_app_backend.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de création / mise à jour d'un rôle.
 *
 * @param name        Nom technique unique (ex. : ROLE_ADMIN).
 * @param description Description humaine.
 */
public record RoleRequest(

        @NotBlank(message = "Le nom du rôle est obligatoire") @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères") String name,

        @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères") String description) {
}

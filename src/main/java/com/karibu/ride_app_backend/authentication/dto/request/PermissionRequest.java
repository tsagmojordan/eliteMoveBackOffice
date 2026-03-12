package com.karibu.ride_app_backend.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * DTO de création / mise à jour d'une permission.
 *
 * @param name        Nom technique unique (ex. : USER_CREATE).
 * @param description Description humaine.
 * @param module      Module applicatif concerné.
 */
public record PermissionRequest(

        @NotBlank(message = "Le nom de la permission est obligatoire") @Size(max = 100, message = "Le nom ne doit pas dépasser 100 caractères") String name,

        @Size(max = 255, message = "La description ne doit pas dépasser 255 caractères") String description,

        @Size(max = 100, message = "Le module ne doit pas dépasser 100 caractères") String module) {
}

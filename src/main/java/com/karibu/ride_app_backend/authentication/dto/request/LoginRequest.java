package com.karibu.ride_app_backend.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;

/**
 * DTO de login (authentification).
 *
 * @param usernameOrEmail Identifiant ou e-mail.
 * @param password        Mot de passe en clair.
 */
public record LoginRequest(

        @NotBlank(message = "L'identifiant ou l'email est obligatoire") String usernameOrEmail,

        @NotBlank(message = "Le mot de passe est obligatoire") String password) {
}

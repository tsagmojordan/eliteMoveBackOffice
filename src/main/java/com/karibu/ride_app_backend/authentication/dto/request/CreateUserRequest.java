package com.karibu.ride_app_backend.authentication.dto.request;

import jakarta.validation.constraints.*;

/**
 * DTO de création d'un utilisateur.
 *
 * @param firstname Prénom.
 * @param lastname  Nom de famille.
 * @param username  Identifiant unique.
 * @param email     Adresse e-mail unique.
 * @param password  Mot de passe en clair (sera hashé).
 */
public record CreateUserRequest(

        @NotBlank(message = "Le prénom est obligatoire") @Size(max = 100) String firstname,

        @NotBlank(message = "Le nom est obligatoire") @Size(max = 100) String lastname,

        @NotBlank(message = "Le nom d'utilisateur est obligatoire") @Size(min = 3, max = 100) String username,

        @NotBlank(message = "L'email est obligatoire") @Email(message = "Format d'email invalide") @Size(max = 150) String email,

        @NotBlank(message = "Le mot de passe est obligatoire") @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères") String password) {
}

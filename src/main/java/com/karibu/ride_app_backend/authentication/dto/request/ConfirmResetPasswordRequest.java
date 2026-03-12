package com.karibu.ride_app_backend.authentication.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ConfirmResetPasswordRequest(
        @NotBlank(message = "Le token est obligatoire") String token,

        @NotBlank(message = "Le nouveau mot de passe est obligatoire") @Size(min = 8, message = "Le mot de passe doit contenir au moins 8 caractères") String newPassword) {
}

package com.karibu.ride_app_backend.authentication.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ResetPasswordRequest(
        @NotBlank(message = "L'adresse e-mail est obligatoire") @Email(message = "L'adresse e-mail doit être valide") String email) {
}

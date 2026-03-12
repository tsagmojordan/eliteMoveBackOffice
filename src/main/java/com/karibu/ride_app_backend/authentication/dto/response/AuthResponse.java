package com.karibu.ride_app_backend.authentication.dto.response;

/**
 * DTO de réponse suite à une authentification réussie.
 *
 * @param accessToken  Jeton JWT d'accès (courte durée de vie).
 * @param refreshToken Jeton de rafraîchissement (longue durée de vie).
 * @param tokenType    Type de jeton (toujours "Bearer").
 * @param expiresIn    Durée de validité en secondes de l'access token.
 * @param user         Informations de l'utilisateur authentifié.
 */
public record AuthResponse(
        String accessToken,
        String refreshToken,
        String tokenType,
        long expiresIn,
        UserResponse user) {
}

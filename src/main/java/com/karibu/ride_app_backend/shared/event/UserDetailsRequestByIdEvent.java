package com.karibu.ride_app_backend.shared.event;

import com.karibu.ride_app_backend.shared.valueobject.UserPayload;

import java.io.Serializable;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Evénement publié pour demander au module d'authentification les détails
 * d'un utilisateur par son identifiant. Le destinataire doit compléter la
 * CompletableFuture avec le UserPayload correspondant.
 */
public record UserDetailsRequestByIdEvent(
        UUID userId,
        CompletableFuture<UserPayload> response
) implements Serializable {
}


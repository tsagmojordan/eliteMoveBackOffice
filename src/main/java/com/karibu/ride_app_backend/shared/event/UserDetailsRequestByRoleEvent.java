package com.karibu.ride_app_backend.shared.event;

import com.karibu.ride_app_backend.shared.valueobject.UserPayload;

import java.io.Serializable;
import java.util.List;
import java.util.concurrent.CompletableFuture;

/**
 * Evénement publié pour demander au module d'authentification la liste des
 * utilisateurs d'un certain rôle. Le destinataire doit compléter la
 * CompletableFuture avec la liste de UserPayload.
 */
public record UserDetailsRequestByRoleEvent(
        String role,
        CompletableFuture<List<UserPayload>> response
) implements Serializable {
}


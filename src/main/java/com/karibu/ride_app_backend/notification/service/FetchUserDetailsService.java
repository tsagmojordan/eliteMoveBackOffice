package com.karibu.ride_app_backend.notification.service;

import com.karibu.ride_app_backend.shared.event.UserDetailsRequestByIdEvent;
import com.karibu.ride_app_backend.shared.event.UserDetailsRequestByRoleEvent;
import com.karibu.ride_app_backend.shared.valueobject.UserPayload;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Service utilitaire pour publier des demandes asynchrones de détails user
 * (request-response via CompletableFuture). Le module d'authentification doit
 * écouter ces événements et compléter les futures.
 */
@Service
public class FetchUserDetailsService {

    private final ApplicationEventPublisher publisher;

    public FetchUserDetailsService(final ApplicationEventPublisher publisher) {
        this.publisher = publisher;
    }

    public CompletableFuture<UserPayload> fetchById(final UUID userId) {
        final CompletableFuture<UserPayload> future = new CompletableFuture<>();
        publisher.publishEvent(new UserDetailsRequestByIdEvent(userId, future));
        return future;
    }

    public CompletableFuture<List<UserPayload>> fetchByRole(final String role) {
        final CompletableFuture<List<UserPayload>> future = new CompletableFuture<>();
        publisher.publishEvent(new UserDetailsRequestByRoleEvent(role, future));
        return future;
    }
}


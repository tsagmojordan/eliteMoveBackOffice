package com.karibu.ride_app_backend.authentication.event.listeners;

import com.karibu.ride_app_backend.authentication.model.User;
import com.karibu.ride_app_backend.authentication.repository.UserRepository;
import com.karibu.ride_app_backend.shared.event.UserDetailsRequestByIdEvent;
import com.karibu.ride_app_backend.shared.event.UserDetailsRequestByRoleEvent;
import com.karibu.ride_app_backend.shared.valueobject.UserPayload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.modulith.events.ApplicationModuleListener;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Listener dédié aux événements de demande de détails utilisateur.
 * Répond aux requêtes du module notification en complétant les CompletableFuture.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class UserDetailsEventListener {

    private final UserRepository userRepository;

    /**
     * Répond à une demande de détails utilisateur par ID.
     */
    @ApplicationModuleListener
    public void onUserDetailsRequestById(final UserDetailsRequestByIdEvent event) {
        log.debug("[UserDetailsEventListener] Demande de détails utilisateur pour id={}", event.userId());

        try {
            final Optional<User> userOptional = userRepository.findById(event.userId());

            if (userOptional.isPresent()) {
                final User user = userOptional.get();
                final UserPayload payload = mapToUserPayload(user);
                log.debug("[UserDetailsEventListener] Utilisateur trouvé ({}). Complétion de la future...", user.getEmail());
                event.response().complete(payload);
            } else {
                final String errorMsg = "Utilisateur non trouvé pour id=" + event.userId();
                log.debug("[UserDetailsEventListener] {}", errorMsg);
                event.response().completeExceptionally(
                        new IllegalArgumentException(errorMsg)
                );
            }
        } catch (Exception ex) {
            log.error("[UserDetailsEventListener] Erreur lors de la récupération de l'utilisateur id={} : {}", event.userId(), ex.getMessage());
            event.response().completeExceptionally(ex);
        }
    }

    /**
     * Répond à une demande de détails utilisateurs par rôle.
     */
    @ApplicationModuleListener
    public void onUserDetailsRequestByRole(final UserDetailsRequestByRoleEvent event) {
        log.debug("[UserDetailsEventListener] Demande de liste utilisateurs pour rôle={}", event.role());

        try {
            final List<User> users = userRepository.findByRoleName(event.role());

            if (!users.isEmpty()) {
                final List<UserPayload> payloads = users.stream()
                        .map(this::mapToUserPayload)
                        .toList();
                log.debug("[UserDetailsEventListener] {} utilisateur(s) trouvé(s) pour le rôle {}. Complétion de la future...", payloads.size(), event.role());
                event.response().complete(payloads);
            } else {
                log.debug("[UserDetailsEventListener] Aucun utilisateur trouvé pour le rôle={}", event.role());
                event.response().complete(List.of()); // Retourner une liste vide plutôt qu'une erreur
            }
        } catch (Exception ex) {
            log.error("[UserDetailsEventListener] Erreur lors de la récupération des utilisateurs du rôle={} : {}", event.role(), ex.getMessage());
            event.response().completeExceptionally(ex);
        }
    }

    /**
     * Convertit une entité User en UserPayload pour le transfer.
     */
    private UserPayload mapToUserPayload(final User user) {
        return new UserPayload(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                extractRoleName(user),
                user.getFirstname(),
                user.getLastname(),
                null  // Phone n'existe pas dans le modèle User actuel, donc null
        );
    }

    /**
     * Extrait le nom de rôle (le premier si plusieurs).
     */
    private String extractRoleName(final User user) {
        if (user.getRoles() == null || user.getRoles().isEmpty()) {
            return null;
        }
        return user.getRoles().stream()
                .findFirst()
                .map(role -> role.getName())
                .orElse(null);
    }
}

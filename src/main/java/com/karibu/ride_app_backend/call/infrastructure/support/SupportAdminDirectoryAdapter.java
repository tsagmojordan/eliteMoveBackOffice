package com.karibu.ride_app_backend.call.infrastructure.support;

import com.karibu.ride_app_backend.authentication.repository.UserRepository;
import com.karibu.ride_app_backend.call.domain.port.out.SupportAdminDirectory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;

/**
 * Adaptateur Infrastructure — Implémente le port {@link SupportAdminDirectory}
 * via le {@link UserRepository} du module d'authentification (module OPEN).
 *
 * <p>
 * Sont considérés « support » les utilisateurs activés portant un rôle dont
 * le nom contient « admin » (ROLE_ADMIN, SUPER_ADMIN, ROLE_SUPER_ADMIN...),
 * en cohérence avec le routage des dashboards côté mobile
 * (UserRole.fromRoleNames fait du matching par inclusion).
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SupportAdminDirectoryAdapter implements SupportAdminDirectory {

    private final UserRepository userRepository;

    @Override
    public List<UUID> findEnabledSupportAdminIds() {
        final List<UUID> ids = userRepository.findEnabledIdsWithAdminRole();
        log.debug("[SupportAdminDirectoryAdapter] {} administrateur(s) de support activé(s)", ids.size());
        return ids;
    }
}
package com.karibu.ride_app_backend.call.application.service;

import com.karibu.ride_app_backend.call.application.port.in.GetSupportAdminIdUseCase;
import com.karibu.ride_app_backend.call.domain.exception.NoSupportAdminAvailableException;
import com.karibu.ride_app_backend.call.domain.model.CallStatus;
import com.karibu.ride_app_backend.call.domain.port.out.CallRepository;
import com.karibu.ride_app_backend.call.domain.port.out.SupportAdminDirectory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Use case : Sélectionner l'administrateur de support à appeler.
 *
 * <p>
 * Stratégie actuelle — tirage aléatoire équitable parmi les
 * administrateurs activés qui ne sont pas déjà en appel. Le point de
 * sélection (ce service) est l'unique endroit où se branchera le futur
 * load balancer : le contrat REST reste identique pour le mobile.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetSupportAdminIdService implements GetSupportAdminIdUseCase {

    /**
     * Statuts d'un appel considéré comme « actif » : l'administrateur
     * participant à un de ces appels est occupé et sorti du tirage.
     */
    private static final List<CallStatus> ACTIVE_CALL_STATUSES = List.of(
            CallStatus.INITIATED,
            CallStatus.RINGING,
            CallStatus.ACCEPTED,
            CallStatus.IN_PROGRESS);

    private final SupportAdminDirectory supportAdminDirectory;
    private final CallRepository callRepository;

    @Override
    @Transactional(readOnly = true)
    public UUID handle() {
        final List<UUID> candidates = supportAdminDirectory.findEnabledSupportAdminIds();

        if (candidates.isEmpty()) {
            log.warn("[GetSupportAdminIdService] Aucun administrateur de support activé en base");
            throw new NoSupportAdminAvailableException(
                    "Aucun administrateur de support n'est actuellement disponible.");
        }

        final Set<UUID> busyParticipants = callRepository.findParticipantIdsByStatuses(ACTIVE_CALL_STATUSES);
        final List<UUID> available = candidates.stream()
                .filter(id -> !busyParticipants.contains(id))
                .toList();

        log.debug("[GetSupportAdminIdService] {} admin(s) support activé(s), {} occupé(s), {} disponible(s)",
                candidates.size(), candidates.size() - available.size(), available.size());

        if (available.isEmpty()) {
            throw new NoSupportAdminAvailableException(
                    "Tous les administrateurs de support sont actuellement en appel.");
        }

        final UUID selected = available.get(ThreadLocalRandom.current().nextInt(available.size()));
        log.info("[GetSupportAdminIdService] Admin de support sélectionné : {}", selected);
        return selected;
    }
}
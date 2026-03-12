package com.karibu.ride_app_backend.call.infrastructure.persistence;

import com.karibu.ride_app_backend.call.domain.model.Call;
import com.karibu.ride_app_backend.call.domain.model.CallStatus;
import com.karibu.ride_app_backend.call.domain.port.out.CallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Adaptateur Infrastructure — Implémente le port {@link CallRepository} via
 * JPA.
 *
 * <p>
 * Fait le pont entre le port domaine et l'interface Spring Data JPA.
 * La couche domaine ne dépend jamais directement de {@link JpaCallRepository}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallRepositoryAdapter implements CallRepository {

    private final JpaCallRepository jpaRepository;

    @Override
    public Call save(final Call call) {
        return jpaRepository.save(call);
    }

    @Override
    public Optional<Call> findById(final UUID id) {
        return jpaRepository.findById(id);
    }

    @Override
    public List<Call> findAllByUserId(final UUID userId) {
        return jpaRepository.findAllByUserId(userId);
    }

    @Override
    public List<Call> findPageByUserId(final UUID userId, final int page, final int size) {
        return jpaRepository.findPageByUserId(userId, PageRequest.of(page, size));
    }

    @Override
    public List<Call> findAllByStatus(final CallStatus status) {
        return jpaRepository.findAllByStatus(status);
    }

    @Override
    public long countMissedCallsByCalleeId(final UUID calleeId) {
        return jpaRepository.countByCalleeIdAndStatus(calleeId, CallStatus.MISSED);
    }
}

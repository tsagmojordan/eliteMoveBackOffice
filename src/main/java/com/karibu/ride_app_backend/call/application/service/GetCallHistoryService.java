package com.karibu.ride_app_backend.call.application.service;

import com.karibu.ride_app_backend.call.application.dto.CallHistoryItem;
import com.karibu.ride_app_backend.call.application.dto.CallSummary;
import com.karibu.ride_app_backend.call.application.mapper.CallApplicationMapper;
import com.karibu.ride_app_backend.call.application.port.in.GetCallHistoryUseCase;
import com.karibu.ride_app_backend.call.domain.exception.CallNotFoundException;
import com.karibu.ride_app_backend.call.domain.model.Call;
import com.karibu.ride_app_backend.call.domain.port.out.CallRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Use case : Consulter l'historique des appels d'un utilisateur.
 *
 * <p>
 * Lecture seule — aucune modification de l'état du domaine.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class GetCallHistoryService implements GetCallHistoryUseCase {

    private final CallRepository callRepository;
    private final CallApplicationMapper mapper;

    @Override
    @Transactional(readOnly = true)
    public List<CallHistoryItem> handle(final UUID userId, final int page, final int size) {
        log.debug("[GetCallHistoryService] Récupération de l'historique (userId={}, page={}, size={})",
                userId, page, size);

        return callRepository.findPageByUserId(userId, page, size)
                .stream()
                .map(mapper::toHistoryItem)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public CallSummary findOne(final UUID callId) {
        log.debug("[GetCallHistoryService] Récupération du résumé de l'appel id={}", callId);

        final Call call = callRepository.findById(callId)
                .orElseThrow(() -> new CallNotFoundException(callId));

        return mapper.toSummary(call);
    }

    @Override
    @Transactional(readOnly = true)
    public long countMissed(final UUID userId) {
        final long count = callRepository.countMissedCallsByCalleeId(userId);
        log.debug("[GetCallHistoryService] {} appel(s) manqué(s) pour userId={}", count, userId);
        return count;
    }
}

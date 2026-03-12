package com.karibu.ride_app_backend.call.application.service;

import com.karibu.ride_app_backend.call.application.port.in.DeclineCallUseCase;
import com.karibu.ride_app_backend.call.domain.exception.CallNotFoundException;
import com.karibu.ride_app_backend.call.domain.exception.InvalidCallStateException;
import com.karibu.ride_app_backend.call.domain.model.Call;
import com.karibu.ride_app_backend.call.domain.port.out.CallRepository;
import com.karibu.ride_app_backend.call.domain.port.out.CallSignalingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Use case : Décliner un appel entrant.
 *
 * <p>
 * Passe l'appel à l'état {@code DECLINED} et notifie l'appelant du refus.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class DeclineCallService implements DeclineCallUseCase {

    private final CallRepository callRepository;
    private final CallSignalingPort signalingPort;

    @Override
    @Transactional
    public void handle(final DeclineCallCommand command) {
        log.info("[DeclineCallService] Refus de l'appel id={} par callee={}",
                command.callId(), command.calleeId());

        final Call call = callRepository.findById(command.callId())
                .orElseThrow(() -> new CallNotFoundException(command.callId()));

        // Règle métier : seul le destinataire peut décliner
        if (!call.getCalleeId().equals(command.calleeId())) {
            throw new InvalidCallStateException(
                    "L'utilisateur " + command.calleeId() + " n'est pas autorisé à décliner cet appel.");
        }

        try {
            call.decline();
            callRepository.save(call);

            // Notifier l'appelant du refus
            signalingPort.notifyCallDeclined(call.getId(), call.getCallerId());

            log.info("[DeclineCallService] Appel id={} décliné.", call.getId());
        } catch (IllegalStateException ex) {
            throw new InvalidCallStateException(ex.getMessage(), ex);
        }
    }
}

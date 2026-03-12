package com.karibu.ride_app_backend.call.application.service;

import com.karibu.ride_app_backend.call.application.port.in.AcceptCallUseCase;
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
 * Use case : Accepter un appel entrant.
 *
 * <p>
 * Vérifie que le destinataire est bien le bon utilisateur,
 * accepte l'appel et notifie l'appelant pour démarrer le WebRTC.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AcceptCallService implements AcceptCallUseCase {

    private final CallRepository callRepository;
    private final CallSignalingPort signalingPort;

    @Override
    @Transactional
    public void handle(final AcceptCallCommand command) {
        log.info("[AcceptCallService] Acceptation de l'appel id={} par callee={}",
                command.callId(), command.calleeId());

        final Call call = callRepository.findById(command.callId())
                .orElseThrow(() -> new CallNotFoundException(command.callId()));

        // Vérification d'autorisation métier : seul le destinataire peut accepter
        if (!call.getCalleeId().equals(command.calleeId())) {
            throw new InvalidCallStateException(
                    "L'utilisateur " + command.calleeId() + " n'est pas le destinataire de l'appel "
                            + command.callId());
        }

        try {
            // Transition domaine
            call.accept();
            callRepository.save(call);

            // Notifier l'appelant que son appel est décroché
            signalingPort.notifyCallAccepted(call.getId(), call.getCallerId());

            log.info("[AcceptCallService] Appel id={} accepté. Session WebRTC en cours d'établissement.", call.getId());
        } catch (IllegalStateException ex) {
            throw new InvalidCallStateException(ex.getMessage(), ex);
        }
    }
}

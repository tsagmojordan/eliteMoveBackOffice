package com.karibu.ride_app_backend.call.application.service;

import com.karibu.ride_app_backend.call.application.port.in.EndCallUseCase;
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
 * Use case : Terminer un appel (raccrocher).
 *
 * <p>
 * Peut être déclenché par l'appelant ou le destinataire.
 * Notifie l'autre participant de la fin de la connexion.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class EndCallService implements EndCallUseCase {

    private final CallRepository callRepository;
    private final CallSignalingPort signalingPort;

    @Override
    @Transactional
    public void handle(final EndCallCommand command) {
        log.info("[EndCallService] Fin de l'appel id={} demandée par userId={}",
                command.callId(), command.requesterId());

        final Call call = callRepository.findById(command.callId())
                .orElseThrow(() -> new CallNotFoundException(command.callId()));

        // Règle métier : seul un participant peut raccrocher
        final boolean isParticipant = call.getCallerId().equals(command.requesterId())
                || call.getCalleeId().equals(command.requesterId());

        if (!isParticipant) {
            throw new InvalidCallStateException(
                    "L'utilisateur " + command.requesterId() + " n'est pas participant à cet appel.");
        }

        if (call.isTerminated()) {
            log.warn("[EndCallService] Appel id={} déjà terminé (statut={}). Ignoré.", call.getId(), call.getStatus());
            return;
        }

        try {
            final String reason = command.reason() != null ? command.reason() : "NORMAL";
            call.end(reason);
            callRepository.save(call);

            // Notifier les deux parties de la fin
            signalingPort.notifyCallEnded(call.getId(), call.getCallerId(), call.getCalleeId());

            log.info("[EndCallService] Appel id={} terminé. Durée: {}s. Raison: {}",
                    call.getId(), call.getDurationSeconds(), reason);
        } catch (IllegalStateException ex) {
            throw new InvalidCallStateException(ex.getMessage(), ex);
        }
    }
}

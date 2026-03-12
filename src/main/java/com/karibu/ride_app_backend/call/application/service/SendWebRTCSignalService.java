package com.karibu.ride_app_backend.call.application.service;

import com.karibu.ride_app_backend.call.application.port.in.SendWebRTCSignalUseCase;
import com.karibu.ride_app_backend.call.domain.exception.CallNotFoundException;
import com.karibu.ride_app_backend.call.domain.exception.InvalidCallStateException;
import com.karibu.ride_app_backend.call.domain.model.Call;
import com.karibu.ride_app_backend.call.domain.port.out.CallRepository;
import com.karibu.ride_app_backend.call.domain.port.out.CallSignalingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class SendWebRTCSignalService implements SendWebRTCSignalUseCase {

    private final CallRepository callRepository;
    private final CallSignalingPort signalingPort;

    @Override
    @Transactional(readOnly = true)
    public void handle(final SendSignalCommand command) {
        final Call call = callRepository.findById(command.callId())
                .orElseThrow(() -> new CallNotFoundException(command.callId()));

        if (call.isTerminated()) {
            throw new InvalidCallStateException("Impossible d'envoyer un signal pour un appel terminé.");
        }

        // Déterminer le destinataire du signal (l'autre participant)
        final UUID recipientId = call.getCallerId().equals(command.senderId())
                ? call.getCalleeId()
                : call.getCallerId();

        // Validation de sécurité : l'expéditeur doit être un participant
        if (!call.getCallerId().equals(command.senderId()) && !call.getCalleeId().equals(command.senderId())) {
            throw new InvalidCallStateException("L'expéditeur n'est pas participant à cet appel.");
        }

        log.debug("[SendWebRTCSignal] Routage du signal de {} vers {}", command.senderId(), recipientId);

        signalingPort.sendWebRTCSignal(call.getId(), recipientId, command.signalPayload());
    }
}

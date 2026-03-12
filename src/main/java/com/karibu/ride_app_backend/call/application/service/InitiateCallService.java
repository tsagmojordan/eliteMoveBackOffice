package com.karibu.ride_app_backend.call.application.service;

import com.karibu.ride_app_backend.call.application.port.in.InitiateCallUseCase;
import com.karibu.ride_app_backend.call.domain.model.Call;
import com.karibu.ride_app_backend.call.domain.model.CallType;
import com.karibu.ride_app_backend.call.domain.port.out.CallRepository;
import com.karibu.ride_app_backend.call.domain.port.out.CallSignalingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Use case : Initier un appel audio.
 *
 * <p>
 * Orchestration :
 * <ol>
 * <li>Création de l'agrégat {@link Call} (état INITIATED).</li>
 * <li>Persistance en base.</li>
 * <li>Transition vers RINGING via la sonnerie mobile.</li>
 * <li>Mise à jour de l'état RINGING en base.</li>
 * </ol>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class InitiateCallService implements InitiateCallUseCase {

    private final CallRepository callRepository;
    private final CallSignalingPort signalingPort;

    @Override
    @Transactional
    public UUID handle(final InitiateCallCommand command) {
        log.info("[InitiateCallService] Initiation d'un appel {} → {}",
                command.callerId(), command.calleeId());

        final CallType callType = CallType.valueOf(command.callType().toUpperCase());

        // 1. Créer l'agrégat (domaine pur)
        final Call call = Call.initiate(command.callerId(), command.calleeId(), callType);

        // 2. Persister en état INITIATED
        final Call savedCall = callRepository.save(call);

        // 3. Transitionner vers RINGING (signal de sonnerie)
        try {
            savedCall.startRinging();
            callRepository.save(savedCall);

            // 4. Envoyer la sonnerie au destinataire (port out)
            signalingPort.notifyIncomingCall(savedCall.getId(), savedCall.getCalleeId(), savedCall.getCallerId());

            log.info("[InitiateCallService] Appel id={} en sonnerie vers callee={}",
                    savedCall.getId(), savedCall.getCalleeId());
        } catch (Exception ex) {
            // Si la signalisation échoue, on marque l'appel comme FAILED
            log.error("[InitiateCallService] Echec de la signalisation pour l'appel id={}. Cause: {}",
                    savedCall.getId(), ex.getMessage());
            savedCall.fail("SIGNALING_ERROR: " + ex.getMessage());
            callRepository.save(savedCall);
            throw ex;
        }

        return savedCall.getId();
    }
}

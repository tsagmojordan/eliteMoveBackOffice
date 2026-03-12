package com.karibu.ride_app_backend.call.infrastructure.scheduler;

import com.karibu.ride_app_backend.call.domain.model.Call;
import com.karibu.ride_app_backend.call.domain.model.CallStatus;
import com.karibu.ride_app_backend.call.domain.port.out.CallRepository;
import com.karibu.ride_app_backend.call.domain.port.out.CallSignalingPort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Scheduler — Détecte et ferme les appels bloqués en état {@code RINGING} non
 * répondus.
 *
 * <p>
 * Un appel passé en {@code RINGING} depuis plus de
 * {@link #RING_TIMEOUT_SECONDS} secondes
 * sans réponse est automatiquement marqué {@code MISSED}.
 *
 * <p>
 * Cette tâche tourne toutes les 30 secondes.
 * En production, considérer un délai configurable via {@code @Value}.
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class CallTimeoutScheduler {

    /** Délai maksimum de sonnerie en secondes (30s par défaut). */
    private static final long RING_TIMEOUT_SECONDS = 30L;

    private final CallRepository callRepository;
    private final CallSignalingPort signalingPort;

    /**
     * Tâche planifiée toutes les 30 secondes.
     * Détecte les appels en sonnerie depuis trop longtemps et les marque MISSED.
     */
    @Transactional
    @Scheduled(fixedDelay = 30_000)
    public void markStaleRingingCallsAsMissed() {
        final List<Call> ringingCalls = callRepository.findAllByStatus(CallStatus.RINGING);

        if (ringingCalls.isEmpty()) {
            return;
        }

        final LocalDateTime cutoff = LocalDateTime.now().minusSeconds(RING_TIMEOUT_SECONDS);
        int count = 0;

        for (final Call call : ringingCalls) {
            if (call.getCreatedAt() != null && call.getCreatedAt().isBefore(cutoff)) {
                try {
                    call.markAsMissed();
                    callRepository.save(call);

                    // Notifier le destinataire que la sonnerie a expiré
                    signalingPort.notifyCallCancelled(call.getId(), call.getCalleeId());
                    count++;

                    log.info("[CallTimeoutScheduler] Appel id={} marqué MISSED (timeout {}s).",
                            call.getId(), RING_TIMEOUT_SECONDS);
                } catch (Exception ex) {
                    log.error("[CallTimeoutScheduler] Erreur lors du traitement de l'appel id={}. Cause: {}",
                            call.getId(), ex.getMessage());
                }
            }
        }

        if (count > 0) {
            log.info("[CallTimeoutScheduler] {} appel(s) marqué(s) MISSED.", count);
        }
    }
}

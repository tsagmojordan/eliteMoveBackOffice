package com.karibu.ride_app_backend.call.application.mapper;

import com.karibu.ride_app_backend.call.application.dto.CallHistoryItem;
import com.karibu.ride_app_backend.call.application.dto.CallSummary;
import com.karibu.ride_app_backend.call.domain.model.Call;
import org.springframework.stereotype.Component;

/**
 * Mapper applicatif — Convertit les agrégats {@link Call} en DTOs internes.
 *
 * <p>
 * Volontairement simple (pas de MapStruct ici) pour garder le domaine
 * sans dépendance sur le framework de mapping.
 */
@Component
public class CallApplicationMapper {

    /**
     * Convertit un agrégat en élément d'historique.
     *
     * @param call L'agrégat source.
     * @return Le DTO {@link CallHistoryItem} correspondant.
     */
    public CallHistoryItem toHistoryItem(final Call call) {
        return new CallHistoryItem(
                call.getId(),
                call.getCallerId(),
                call.getCalleeId(),
                call.getCallType(),
                call.getStatus(),
                call.getCreatedAt(),
                call.getAnsweredAt(),
                call.getEndedAt(),
                call.getDurationSeconds(),
                call.getEndReason());
    }

    /**
     * Convertit un agrégat en résumé détaillé.
     *
     * @param call L'agrégat source.
     * @return Le DTO {@link CallSummary} correspondant.
     */
    public CallSummary toSummary(final Call call) {
        return new CallSummary(
                call.getId(),
                call.getCallerId(),
                call.getCalleeId(),
                call.getCallType(),
                call.getStatus(),
                call.getCreatedAt(),
                call.getAnsweredAt(),
                call.getEndedAt(),
                call.getDurationSeconds(),
                call.getEndReason(),
                call.isActive(),
                call.isTerminated());
    }
}

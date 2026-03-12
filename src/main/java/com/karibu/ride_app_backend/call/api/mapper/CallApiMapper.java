package com.karibu.ride_app_backend.call.api.mapper;

import com.karibu.ride_app_backend.call.api.dto.response.CallResponse;
import com.karibu.ride_app_backend.call.application.dto.CallHistoryItem;
import com.karibu.ride_app_backend.call.application.dto.CallSummary;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Mapper API — Convertit les DTOs applicatifs internes en DTOs de réponse REST.
 *
 * <p>
 * Ce mapper est côté API et ne connaît pas les agrégats du domaine.
 * Il traduit uniquement les DTOs application → DTOs HTTP.
 */
@Component
public class CallApiMapper {

    /**
     * Convertit un résumé applicatif en réponse REST.
     *
     * @param summary DTO interne.
     * @return DTO HTTP.
     */
    public CallResponse toResponse(final CallSummary summary) {
        return new CallResponse(
                summary.id(),
                summary.callerId(),
                summary.calleeId(),
                summary.callType(),
                summary.status(),
                summary.createdAt(),
                summary.answeredAt(),
                summary.endedAt(),
                summary.durationSeconds(),
                summary.endReason(),
                summary.isActive(),
                summary.isTerminated());
    }

    /**
     * Convertit un élément d'historique applicatif en réponse REST.
     *
     * @param item DTO interne d'historique.
     * @return DTO HTTP.
     */
    public CallResponse toResponse(final CallHistoryItem item) {
        return new CallResponse(
                item.id(),
                item.callerId(),
                item.calleeId(),
                item.callType(),
                item.status(),
                item.createdAt(),
                item.answeredAt(),
                item.endedAt(),
                item.durationSeconds(),
                item.endReason(),
                false,
                true);
    }

    /**
     * Convertit une liste d'éléments d'historique.
     *
     * @param items Liste interne.
     * @return Liste HTTP.
     */
    public List<CallResponse> toResponseList(final List<CallHistoryItem> items) {
        return items.stream().map(this::toResponse).toList();
    }
}

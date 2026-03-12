package com.karibu.ride_app_backend.call.api.controller;

import com.karibu.ride_app_backend.call.api.dto.request.EndCallRequest;
import com.karibu.ride_app_backend.call.api.dto.request.InitiateCallRequest;
import com.karibu.ride_app_backend.call.api.dto.response.CallResponse;
import com.karibu.ride_app_backend.call.api.dto.response.InitiateCallResponse;
import com.karibu.ride_app_backend.call.api.mapper.CallApiMapper;
import com.karibu.ride_app_backend.authentication.model.User;
import com.karibu.ride_app_backend.call.application.dto.CallHistoryItem;
import com.karibu.ride_app_backend.call.application.dto.CallSummary;
import com.karibu.ride_app_backend.call.application.port.in.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Contrôleur REST — Module Call.
 *
 * <p>
 * Expose les endpoints de gestion du cycle de vie des appels audio.
 * Tous les endpoints nécessitent une authentification JWT.
 *
 * <p>
 * Base URL : {@code /api/v1/calls}
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/calls")
@RequiredArgsConstructor
@Tag(name = "Call", description = "Gestion des appels audio entre utilisateurs")
public class CallController {

    private final InitiateCallUseCase initiateCallUseCase;
    private final AcceptCallUseCase acceptCallUseCase;
    private final DeclineCallUseCase declineCallUseCase;
    private final EndCallUseCase endCallUseCase;
    private final GetCallHistoryUseCase getCallHistoryUseCase;
    private final SendWebRTCSignalUseCase sendWebRTCSignalUseCase;
    private final CallApiMapper mapper;

    // =========================================================================
    // Initier un appel
    // =========================================================================

    /**
     * POST /api/v1/calls
     * Initie un appel vers un autre utilisateur.
     */
    @PostMapping
    @Operation(summary = "Initier un appel", description = "Crée un appel et envoie la sonnerie au destinataire.")
    public ResponseEntity<InitiateCallResponse> initiateCall(
            @Valid @RequestBody final InitiateCallRequest request,
            @AuthenticationPrincipal final UserDetails userDetails) {

        final UUID callerId = extractUserId(userDetails);
        log.info("[CallController] POST /api/v1/calls — caller={} → callee={}", callerId, request.calleeId());

        final UUID callId = initiateCallUseCase.handle(
                new InitiateCallUseCase.InitiateCallCommand(callerId, request.calleeId(), request.callType()));

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new InitiateCallResponse(callId, "Appel initié. Sonnerie envoyée au destinataire."));
    }

    // =========================================================================
    // Accepter un appel
    // =========================================================================

    /**
     * PATCH /api/v1/calls/{callId}/accept
     * Accepte un appel entrant.
     */
    @PatchMapping("/{callId}/accept")
    @Operation(summary = "Accepter un appel", description = "Le destinataire accepte l'appel entrant.")
    public ResponseEntity<Void> acceptCall(
            @Parameter(description = "Identifiant de l'appel") @PathVariable final UUID callId,
            @AuthenticationPrincipal final UserDetails userDetails) {

        final UUID calleeId = extractUserId(userDetails);
        log.info("[CallController] PATCH /api/v1/calls/{}/accept — callee={}", callId, calleeId);

        acceptCallUseCase.handle(new AcceptCallUseCase.AcceptCallCommand(callId, calleeId));
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // Décliner un appel
    // =========================================================================

    /**
     * PATCH /api/v1/calls/{callId}/decline
     * Décline un appel entrant.
     */
    @PatchMapping("/{callId}/decline")
    @Operation(summary = "Décliner un appel", description = "Le destinataire refuse l'appel entrant.")
    public ResponseEntity<Void> declineCall(
            @Parameter(description = "Identifiant de l'appel") @PathVariable final UUID callId,
            @AuthenticationPrincipal final UserDetails userDetails) {

        final UUID calleeId = extractUserId(userDetails);
        log.info("[CallController] PATCH /api/v1/calls/{}/decline — callee={}", callId, calleeId);

        declineCallUseCase.handle(new DeclineCallUseCase.DeclineCallCommand(callId, calleeId));
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // Terminer un appel
    // =========================================================================

    /**
     * PATCH /api/v1/calls/{callId}/end
     * Termine un appel (raccrocher).
     */
    @PatchMapping("/{callId}/end")
    @Operation(summary = "Terminer un appel", description = "L'un des participants raccroche.")
    public ResponseEntity<Void> endCall(
            @Parameter(description = "Identifiant de l'appel") @PathVariable final UUID callId,
            @RequestBody(required = false) final EndCallRequest request,
            @AuthenticationPrincipal final UserDetails userDetails) {

        final UUID requesterId = extractUserId(userDetails);
        final String reason = (request != null && request.reason() != null) ? request.reason() : "NORMAL";

        log.info("[CallController] PATCH /api/v1/calls/{}/end — requester={}, reason={}", callId, requesterId, reason);

        endCallUseCase.handle(new EndCallUseCase.EndCallCommand(callId, requesterId, reason));
        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // Signalisation WebRTC
    // =========================================================================

    /**
     * POST /api/v1/calls/signaling
     * Relaye les messages de négociation WebRTC (Offer, Answer, ICE Candidates).
     */
    @PostMapping("/signaling")
    @Operation(summary = "Relayer un signal WebRTC", description = "Transmet un message de négociation (SDP/ICE) à l'autre participant.")
    public ResponseEntity<Void> sendSignaling(
            @Valid @RequestBody final com.karibu.ride_app_backend.call.api.dto.request.WebRTCSignalRequest request,
            @AuthenticationPrincipal final UserDetails userDetails) {

        final UUID senderId = extractUserId(userDetails);
        log.debug("[CallController] POST /api/v1/calls/signaling — sender={}", senderId);

        sendWebRTCSignalUseCase.handle(new SendWebRTCSignalUseCase.SendSignalCommand(
                request.callId(), senderId, request.signal()));

        return ResponseEntity.noContent().build();
    }

    // =========================================================================
    // Historique des appels
    // =========================================================================

    /**
     * GET /api/v1/calls/history?page=0&size=20
     * Retourne l'historique paginé des appels de l'utilisateur connecté.
     */
    @GetMapping("/history")
    @Operation(summary = "Historique des appels", description = "Retourne l'historique paginé des appels de l'utilisateur.")
    public ResponseEntity<List<CallResponse>> getHistory(
            @RequestParam(defaultValue = "0") final int page,
            @RequestParam(defaultValue = "20") final int size,
            @AuthenticationPrincipal final UserDetails userDetails) {

        final UUID userId = extractUserId(userDetails);
        log.debug("[CallController] GET /api/v1/calls/history — userId={}, page={}, size={}", userId, page, size);

        final List<CallHistoryItem> items = getCallHistoryUseCase.handle(userId, page, size);
        return ResponseEntity.ok(mapper.toResponseList(items));
    }

    /**
     * GET /api/v1/calls/{callId}
     * Retourne le détail d'un appel spécifique.
     */
    @GetMapping("/{callId}")
    @Operation(summary = "Détail d'un appel", description = "Retourne les détails d'un appel par son identifiant.")
    public ResponseEntity<CallResponse> getCallById(
            @Parameter(description = "Identifiant de l'appel") @PathVariable final UUID callId) {

        log.debug("[CallController] GET /api/v1/calls/{}", callId);

        final CallSummary summary = getCallHistoryUseCase.findOne(callId);
        return ResponseEntity.ok(mapper.toResponse(summary));
    }

    /**
     * GET /api/v1/calls/missed/count
     * Retourne le nombre d'appels manqués de l'utilisateur connecté.
     */
    @GetMapping("/missed/count")
    @Operation(summary = "Compteur d'appels manqués", description = "Retourne le nombre d'appels manqués non vus.")
    public ResponseEntity<Map<String, Long>> countMissedCalls(
            @AuthenticationPrincipal final UserDetails userDetails) {

        final UUID userId = extractUserId(userDetails);
        final long count = getCallHistoryUseCase.countMissed(userId);
        return ResponseEntity.ok(Map.of("missedCalls", count));
    }

    // =========================================================================
    // Helper privé
    // =========================================================================

    /**
     * Extrait l'UUID de l'utilisateur connecté depuis le principal Spring Security.
     *
     * @param userDetails Principal Spring Security.
     * @return UUID de l'utilisateur.
     */
    private UUID extractUserId(final UserDetails userDetails) {
        if (userDetails instanceof User user) {
            return user.getId();
        }
        throw new IllegalStateException("UserDetails n'est pas une instance de User");
    }
}

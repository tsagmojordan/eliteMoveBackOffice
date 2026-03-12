package com.karibu.ride_app_backend.call.domain.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Agrégat racine représentant un appel audio entre deux utilisateurs.
 *
 * <p>
 * Contient toute la logique métier relative au cycle de vie d'un appel :
 * <ul>
 * <li>Transition d'états (machine à états)</li>
 * <li>Calcul de la durée</li>
 * <li>Règles métier (ex : ne peut accepter un appel déjà terminé)</li>
 * </ul>
 *
 * <p>
 * Cette entité est la seule frontière de cohérence pour les opérations d'appel.
 * Aucun service externe ne modifie directement ses champs.
 */
@Entity
@Table(name = "calls")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Call {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Identifiant de l'utilisateur initiateur de l'appel. */
    @Column(name = "caller_id", nullable = false, updatable = false)
    private UUID callerId;

    /** Identifiant de l'utilisateur destinataire. */
    @Column(name = "callee_id", nullable = false, updatable = false)
    private UUID calleeId;

    /** Type d'appel (AUDIO ou VIDEO). */
    @Enumerated(EnumType.STRING)
    @Column(name = "call_type", nullable = false, updatable = false)
    private CallType callType;

    /** Statut courant de l'appel. */
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private CallStatus status;

    /** Horodatage de la création de l'appel. */
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    /** Horodatage auquel le destinataire a décroché. */
    @Column(name = "answered_at")
    private LocalDateTime answeredAt;

    /** Horodatage de fin d'appel (normal, refus, missed ou échec). */
    @Column(name = "ended_at")
    private LocalDateTime endedAt;

    /** Durée de l'appel en secondes (null si l'appel n'a pas abouti). */
    @Column(name = "duration_seconds")
    private Long durationSeconds;

    /**
     * Raison de fin d'appel (libre, ex: "NORMAL", "NO_ANSWER", "NETWORK_ERROR").
     */
    @Column(name = "end_reason", length = 100)
    private String endReason;

    // =========================================================================
    // Factories
    // =========================================================================

    /**
     * Crée un nouvel appel en état {@code INITIATED}.
     *
     * @param callerId Identifiant de l'appelant.
     * @param calleeId Identifiant du destinataire.
     * @param callType Type d'appel (AUDIO / VIDEO).
     * @return Un nouvel agrégat {@code Call} initialisé.
     */
    public static Call initiate(final UUID callerId, final UUID calleeId, final CallType callType) {
        return Call.builder()
                .callerId(callerId)
                .calleeId(calleeId)
                .callType(callType)
                .status(CallStatus.INITIATED)
                .build();
    }

    // =========================================================================
    // Transitions d'état (comportements métier)
    // =========================================================================

    /**
     * Passe l'appel en état {@code RINGING} lorsque la sonnerie est envoyée.
     *
     * @throws IllegalStateException si l'appel n'est pas en état {@code INITIATED}.
     */
    public void startRinging() {
        assertStatus(CallStatus.INITIATED, "démarrer la sonnerie");
        this.status = CallStatus.RINGING;
    }

    /**
     * Accepte l'appel — passage en état {@code ACCEPTED}.
     *
     * @throws IllegalStateException si l'appel n'est pas en état {@code RINGING}.
     */
    public void accept() {
        assertStatus(CallStatus.RINGING, "accepter l'appel");
        this.status = CallStatus.ACCEPTED;
        this.answeredAt = LocalDateTime.now();
    }

    /**
     * Marque l'appel comme en cours ({@code IN_PROGRESS}).
     * Appelé lorsque la connexion WebRTC est établie.
     *
     * @throws IllegalStateException si l'appel n'est pas en état {@code ACCEPTED}.
     */
    public void startProgress() {
        assertStatus(CallStatus.ACCEPTED, "démarrer la session");
        this.status = CallStatus.IN_PROGRESS;
    }

    /**
     * Termine l'appel normalement.
     *
     * @param reason Raison de fin (libre).
     * @throws IllegalStateException si l'appel n'est pas en état actif.
     */
    public void end(final String reason) {
        assertOneOf("terminer l'appel",
                CallStatus.IN_PROGRESS, CallStatus.ACCEPTED, CallStatus.RINGING, CallStatus.INITIATED);
        this.status = CallStatus.ENDED;
        this.endedAt = LocalDateTime.now();
        this.endReason = reason;
        computeDuration();
    }

    /**
     * Décline l'appel (refus explicite du destinataire).
     *
     * @throws IllegalStateException si l'appel n'est pas en état {@code RINGING}.
     */
    public void decline() {
        assertStatus(CallStatus.RINGING, "décliner l'appel");
        this.status = CallStatus.DECLINED;
        this.endedAt = LocalDateTime.now();
        this.endReason = "DECLINED_BY_CALLEE";
    }

    /**
     * Marque l'appel comme manqué (timeout, aucune réponse).
     *
     * @throws IllegalStateException si l'appel n'est pas en état {@code RINGING}.
     */
    public void markAsMissed() {
        assertStatus(CallStatus.RINGING, "marquer comme manqué");
        this.status = CallStatus.MISSED;
        this.endedAt = LocalDateTime.now();
        this.endReason = "NO_ANSWER";
    }

    /**
     * Marque l'appel comme échoué suite à une erreur technique.
     *
     * @param reason Description de l'erreur.
     */
    public void fail(final String reason) {
        this.status = CallStatus.FAILED;
        this.endedAt = LocalDateTime.now();
        this.endReason = reason;
    }

    // =========================================================================
    // Requêtes métier
    // =========================================================================

    /** @return {@code true} si l'appel est dans un état terminal. */
    public boolean isTerminated() {
        return switch (this.status) {
            case ENDED, MISSED, DECLINED, FAILED -> true;
            default -> false;
        };
    }

    /** @return {@code true} si l'appel est actuellement actif (voix établie). */
    public boolean isActive() {
        return this.status == CallStatus.IN_PROGRESS;
    }

    // =========================================================================
    // Helpers privés
    // =========================================================================

    private void assertStatus(final CallStatus expected, final String action) {
        if (this.status != expected) {
            throw new IllegalStateException(
                    String.format("Impossible de %s : statut actuel = %s, statut attendu = %s",
                            action, this.status, expected));
        }
    }

    private void assertOneOf(final String action, final CallStatus... allowed) {
        for (final CallStatus s : allowed) {
            if (this.status == s)
                return;
        }
        throw new IllegalStateException(
                String.format("Impossible de %s : statut '%s' non autorisé.", action, this.status));
    }

    private void computeDuration() {
        if (this.answeredAt != null && this.endedAt != null) {
            this.durationSeconds = Duration.between(this.answeredAt, this.endedAt).getSeconds();
        }
    }
}

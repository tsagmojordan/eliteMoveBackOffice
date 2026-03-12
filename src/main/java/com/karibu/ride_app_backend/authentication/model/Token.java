package com.karibu.ride_app_backend.authentication.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité représentant un jeton JWT persisté en base.
 *
 * <p>
 * Permet de gérer la révocation et le refresh des tokens.
 * Un token révoqué ou expiré ne peut plus être utilisé.
 */
@Entity
@Table(name = "tokens")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Token {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Valeur brute du JWT. */
    @Column(name = "token_value", nullable = false, unique = true, length = 2048)
    private String tokenValue;

    @Enumerated(EnumType.STRING)
    @Column(name = "token_type", nullable = false, length = 20)
    @Builder.Default
    private TokenType tokenType = TokenType.BEARER;

    /** Indique si le token a été explicitement révoqué (logout, rotation). */
    @Column(name = "revoked", nullable = false)
    @Builder.Default
    private boolean revoked = false;

    /** Indique si le token est expiré (usage futur pour gestion fine). */
    @Column(name = "expired", nullable = false)
    @Builder.Default
    private boolean expired = false;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    /** Propriétaire du token. */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    /** Indique si le token est encore valide (non révoqué et non expiré). */
    public boolean isValid() {
        return !revoked && !expired;
    }

    public enum TokenType {
        BEARER,
        RESET_PASSWORD
    }
}

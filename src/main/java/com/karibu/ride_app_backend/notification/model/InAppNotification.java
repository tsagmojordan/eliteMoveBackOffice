package com.karibu.ride_app_backend.notification.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Entité représentant une notification interne (In-App) pour un utilisateur.
 */
@Entity
@Table(name = "in_app_notifications")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InAppNotification {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "recipient_id", nullable = false)
    private UUID recipientId;

    @Column(name = "subject", nullable = false)
    private String subject;

    @Column(name = "message", nullable = false, length = 1000)
    private String message;

    @Column(name = "is_read", nullable = false)
    @Builder.Default
    private boolean read = false;

    @Column(name = "priority", length = 50)
    private String priority;

    @Column(name = "template_code")
    private String templateCode;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;
}

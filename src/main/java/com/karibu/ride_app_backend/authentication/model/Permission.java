package com.karibu.ride_app_backend.authentication.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Entité représentant une permission (granule d'accès).
 *
 * <p>
 * Une permission porte une action (ex. : USER_CREATE, LAMP_READ)
 * associée à une ressource. Elle est ensuite assignée à des rôles.
 */
@Entity
@Table(name = "permissions", uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Permission {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Nom technique unique. Ex : USER_CREATE, LAMP_READ */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** Description humaine de la permission. */
    @Column(name = "description", length = 255)
    private String description;

    /** Module applicatif concerné (ex. : AUTHENTICATION, LIGHTING). */
    @Column(name = "module", length = 100)
    private String module;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Association inverse : rôles qui portent cette permission. */
    @ManyToMany(mappedBy = "permissions", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<Role> roles = new HashSet<>();
}

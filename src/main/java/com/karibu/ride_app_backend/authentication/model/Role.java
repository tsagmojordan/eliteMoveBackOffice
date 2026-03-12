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
 * Entité représentant un rôle fonctionnel.
 *
 * <p>
 * Un rôle agrège un ensemble de {@link Permission}s et est assigné
 * à un ou plusieurs {@link User}s.
 */
@Entity
@Table(name = "roles", uniqueConstraints = @UniqueConstraint(columnNames = { "name" }))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    /** Nom technique unique du rôle. Ex : ROLE_ADMIN, ROLE_OPERATOR */
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    /** Description humaine du rôle. */
    @Column(name = "description", length = 255)
    private String description;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Permissions associées à ce rôle. */
    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "role_permissions", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    @Builder.Default
    private Set<Permission> permissions = new HashSet<>();

    /** Utilisateurs portant ce rôle. */
    @ManyToMany(mappedBy = "roles", fetch = FetchType.LAZY)
    @Builder.Default
    private Set<User> users = new HashSet<>();
}

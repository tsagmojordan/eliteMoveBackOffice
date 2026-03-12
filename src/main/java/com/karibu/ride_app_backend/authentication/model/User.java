package com.karibu.ride_app_backend.authentication.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Entité représentant un utilisateur de l'application.
 *
 * <p>
 * Implémente {@link UserDetails} pour s'intégrer nativement avec Spring
 * Security.
 * Les autorités sont dérivées dynamiquement des rôles et de leurs permissions.
 */
@Entity
@Table(name = "users", uniqueConstraints = {
        @UniqueConstraint(columnNames = { "email" }),
        @UniqueConstraint(columnNames = { "username" })
})
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "firstname", nullable = false, length = 100)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "email", nullable = false, length = 150)
    private String email;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "enabled", nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "account_non_expired", nullable = false)
    @Builder.Default
    private boolean accountNonExpired = true;

    @Column(name = "account_non_locked", nullable = false)
    @Builder.Default
    private boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired", nullable = false)
    @Builder.Default
    private boolean credentialsNonExpired = true;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /** Rôles assignés à cet utilisateur. */
    @ManyToMany(fetch = FetchType.EAGER, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    @Builder.Default
    private Set<Role> roles = new HashSet<>();

    /**
     * Construit les autorités Spring Security à partir des rôles
     * et de toutes les permissions portées par ces rôles.
     *
     * <p>
     * Convention : un rôle produit "ROLE_ADMIN", une permission produit
     * "USER_CREATE".
     */
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        final Stream<SimpleGrantedAuthority> roleAuthorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()));

        final Stream<SimpleGrantedAuthority> permissionAuthorities = roles.stream()
                .flatMap(role -> role.getPermissions().stream())
                .map(permission -> new SimpleGrantedAuthority(permission.getName()));

        return Stream.concat(roleAuthorities, permissionAuthorities)
                .collect(Collectors.toSet());
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }

    @Override
    public boolean isAccountNonExpired() {
        return accountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return accountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return credentialsNonExpired;
    }
}

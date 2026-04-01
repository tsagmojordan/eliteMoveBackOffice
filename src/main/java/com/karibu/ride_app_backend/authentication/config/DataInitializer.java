package com.karibu.ride_app_backend.authentication.config;

import com.karibu.ride_app_backend.authentication.model.Permission;
import com.karibu.ride_app_backend.authentication.model.Role;
import com.karibu.ride_app_backend.authentication.model.User;
import com.karibu.ride_app_backend.authentication.repository.PermissionRepository;
import com.karibu.ride_app_backend.authentication.repository.RoleRepository;
import com.karibu.ride_app_backend.authentication.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

/**
 * Initialiseur de données de démarrage.
 *
 * <p>
 * Crée les permissions, rôles et l'utilisateur admin par défaut
 * si la base est vide. Idempotent (ne recrée rien si déjà présent).
 *
 * <p>
 * <strong>À remplacer par Flyway/Liquibase en production.</strong>
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final PermissionRepository permissionRepository;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(final String... args) {
        log.debug("[DataInitializer] Initialisation des données de démarrage");

        if (permissionRepository.count() > 0) {
            log.debug("[DataInitializer] Données déjà présentes — initialisation ignorée");
            return;
        }

        final Set<Permission> adminPermissions = createPermissions();
        final Role adminRole = createAdminRole(adminPermissions);
        createAdminUser(adminRole);

        //final Set<Permission> userPermissions = createPermissions();
        final Role userRole = createUserRole(adminPermissions);
        createUser(userRole);

        log.debug("[DataInitializer] Initialisation terminée avec succès");
    }

    private Set<Permission> createPermissions() {
        log.debug("[DataInitializer] Création des permissions système");

        final Permission[] permissions = {
                buildPermission("USER_READ", "Lire les utilisateurs", "AUTHENTICATION"),
                buildPermission("USER_CREATE", "Créer des utilisateurs", "AUTHENTICATION"),
                buildPermission("USER_UPDATE", "Modifier des utilisateurs", "AUTHENTICATION"),
                buildPermission("USER_DELETE", "Supprimer des utilisateurs", "AUTHENTICATION"),
                buildPermission("USER_MANAGE_ROLES", "Gérer les rôles d'un user", "AUTHENTICATION"),
                buildPermission("ROLE_READ", "Lire les rôles", "AUTHENTICATION"),
                buildPermission("ROLE_CREATE", "Créer des rôles", "AUTHENTICATION"),
                buildPermission("ROLE_UPDATE", "Modifier des rôles", "AUTHENTICATION"),
                buildPermission("ROLE_DELETE", "Supprimer des rôles", "AUTHENTICATION"),
                buildPermission("ROLE_MANAGE_PERMISSIONS", "Gérer les permissions d'un rôle", "AUTHENTICATION"),
                buildPermission("PERMISSION_READ", "Lire les permissions", "AUTHENTICATION"),
                buildPermission("PERMISSION_CREATE", "Créer des permissions", "AUTHENTICATION"),
                buildPermission("PERMISSION_UPDATE", "Modifier des permissions", "AUTHENTICATION"),
                buildPermission("PERMISSION_DELETE", "Supprimer des permissions", "AUTHENTICATION"),
        };

        final Set<Permission> saved = Set.copyOf(permissionRepository.saveAll(Set.of(permissions)));
        log.debug("[DataInitializer] {} permission(s) créée(s)", saved.size());
        return saved;
    }

    private Role createAdminRole(final Set<Permission> permissions) {
        log.debug("[DataInitializer] Création du rôle ROLE_ADMIN");

        final Role adminRole = Role.builder()
                .name("ROLE_ADMIN")
                .description("Administrateur système — accès total")
                .permissions(permissions)
                .build();

        final Role saved = roleRepository.save(adminRole);
        log.debug("[DataInitializer] Rôle ROLE_ADMIN créé avec {} permission(s)", permissions.size());
        return saved;
    }

    private Role createUserRole(final Set<Permission> permissions) {
        log.debug("[DataInitializer] Création du rôle USER");

        final Role userRole = Role.builder()
                .name("USER")
                .description("Client de l'application elite move")
                .permissions(permissions)
                .build();

        final Role saved = roleRepository.save(userRole);
        log.debug("[DataInitializer] Rôle USER créé avec {} permission(s)", permissions.size());
        return saved;
    }


    private void createAdminUser(final Role adminRole) {
        log.debug("[DataInitializer] Création de l'utilisateur admin par défaut");

        final User admin = User.builder()
                .firstname("Admin")
                .lastname("System")
                .username("admin")
                .email("admin@smartlighting.cm")
                .password(passwordEncoder.encode("Admin@1234"))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(Set.of(adminRole))
                .build();

        userRepository.save(admin);
        log.debug("[DataInitializer] Utilisateur admin créé : admin@smartlighting.cm / Admin@1234");
    }

    private void createUser(final Role userRole) {
        log.debug("[DataInitializer] Création de l'utilisateur admin par défaut");

        final User user = User.builder()
                .firstname("user")
                .lastname("user")
                .username("user")
                .email("user@smartlighting.cm")
                .password(passwordEncoder.encode("User@1234"))
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .roles(Set.of(userRole))
                .build();

        userRepository.save(user);
        log.debug("[DataInitializer] Utilisateur admin créé : user@smartlighting.cm / User@1234");
    }

    private Permission buildPermission(
            final String name,
            final String description,
            final String module) {
        return Permission.builder()
                .name(name)
                .description(description)
                .module(module)
                .build();
    }
}

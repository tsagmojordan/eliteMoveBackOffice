package com.karibu.ride_app_backend.authentication.service;

import com.karibu.ride_app_backend.authentication.dto.request.AssignRolesRequest;
import com.karibu.ride_app_backend.authentication.dto.request.CreateUserRequest;
import com.karibu.ride_app_backend.authentication.dto.response.UserResponse;
import com.karibu.ride_app_backend.authentication.mapper.UserMapper;
import com.karibu.ride_app_backend.authentication.model.Role;
import com.karibu.ride_app_backend.authentication.model.User;
import com.karibu.ride_app_backend.authentication.repository.UserRepository;
import com.karibu.ride_app_backend.authentication.utils.AuthenticationExceptions.ResourceAlreadyExistsException;
import com.karibu.ride_app_backend.authentication.utils.AuthenticationExceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

import com.karibu.ride_app_backend.shared.event.UserCreatedEvent;
import com.karibu.ride_app_backend.shared.event.NotificationRequestedEvent;
import com.karibu.ride_app_backend.shared.enums.NotificationChannel;
import com.karibu.ride_app_backend.shared.enums.NotificationPriority;
import org.springframework.context.ApplicationEventPublisher;
import java.util.Map;

/**
 * Service métier pour la gestion des utilisateurs.
 *
 * <p>
 * Orchestre la création, la modification et la gestion des accès des
 * utilisateurs.
 * L'encodage du mot de passe est délégué au {@link PasswordEncoder} de Spring
 * Security.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleService roleService;
    private final PasswordEncoder passwordEncoder;
    private final ApplicationEventPublisher eventPublisher;

    /**
     * Retourne tous les utilisateurs.
     *
     * @return Liste de tous les utilisateurs.
     */
    public List<UserResponse> findAll() {
        log.debug("[UserService] Récupération de tous les utilisateurs");
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponse)
                .toList();
    }

    /**
     * Retourne tous les utilisateurs en format paginé.
     */
    public com.karibu.ride_app_backend.shared.dto.PaginatedResponse<UserResponse> findAll(String search,
            org.springframework.data.domain.Pageable pageable) {
        log.debug("[UserService] Recherche des utilisateurs avec search={} et page={}", search,
                pageable.getPageNumber());
        org.springframework.data.domain.Page<User> pageResult = userRepository.searchUsers(search, pageable);
        return com.karibu.ride_app_backend.shared.dto.PaginatedResponse.from(pageResult.map(userMapper::toResponse));
    }

    /**
     * Retourne un utilisateur par son identifiant.
     *
     * @param id UUID de l'utilisateur.
     * @return DTO de réponse.
     */
    public UserResponse findById(final UUID id) {
        log.debug("[UserService] Recherche de l'utilisateur id={}", id);
        return userRepository.findById(id)
                .map(userMapper::toResponse)
                .orElseThrow(() -> {
                    log.debug("[UserService] Utilisateur introuvable id={}", id);
                    return new ResourceNotFoundException("Utilisateur introuvable avec l'id : " + id);
                });
    }

    /**
     * Crée un nouvel utilisateur.
     *
     * <p>
     * Vérifie l'unicité du username ET de l'email avant la création,
     * puis encode le mot de passe avec BCrypt.
     *
     * @param request DTO de création.
     * @return DTO de l'utilisateur créé.
     */
    @Transactional
    public UserResponse create(final CreateUserRequest request) {
        log.debug("[UserService] Création de l'utilisateur : {}", request.username());
        assertUsernameAndEmailNotExists(request.username(), request.email());

        final User user = userMapper.toEntity(request);
        user.setPassword(passwordEncoder.encode(request.password()));

        final User saved = userRepository.save(user);

        eventPublisher.publishEvent(UserCreatedEvent.of(
                saved.getId(),
                saved.getUsername(),
                saved.getEmail(),
                saved.getFirstname(),
                saved.getLastname()));

        log.debug("[UserService] Utilisateur créé avec succès : id={}", saved.getId());
        return userMapper.toResponse(saved);
    }

    /**
     * Active ou désactive un compte utilisateur.
     *
     * @param id      UUID de l'utilisateur.
     * @param enabled Nouvel état du compte.
     * @return DTO de l'utilisateur mis à jour.
     */
    @Transactional
    public UserResponse setEnabled(final UUID id, final boolean enabled) {
        log.debug("[UserService] Changement de statut du compte id={} -> enabled={}", id, enabled);
        final User user = findUserEntityById(id);
        user.setEnabled(enabled);
        final User saved = userRepository.save(user);

        eventPublisher.publishEvent(NotificationRequestedEvent.of(
                saved.getId(),
                saved.getEmail(),
                null,
                saved.getFirstname() + " " + saved.getLastname(),
                Set.of(NotificationChannel.EMAIL, NotificationChannel.IN_APP),
                NotificationPriority.NORMAL,
                "USER_STATUS_CHANGED",
                Map.of("username", saved.getUsername(), "status", enabled ? "Activé" : "Désactivé"),
                "Modification du statut de votre compte"));

        log.debug("[UserService] Statut du compte mis à jour : id={}", id);
        return userMapper.toResponse(saved);
    }

    /**
     * Supprime un utilisateur.
     *
     * @param id UUID de l'utilisateur.
     */
    @Transactional
    public void delete(final UUID id) {
        log.debug("[UserService] Suppression de l'utilisateur id={}", id);
        final User user = findUserEntityById(id);

        eventPublisher.publishEvent(NotificationRequestedEvent.of(
                user.getId(),
                user.getEmail(),
                null,
                user.getFirstname() + " " + user.getLastname(),
                Set.of(NotificationChannel.EMAIL),
                NotificationPriority.HIGH,
                "USER_DELETED",
                Map.of("username", user.getUsername()),
                "Suppression de votre compte"));

        userRepository.delete(user);
        log.debug("[UserService] Utilisateur supprimé avec succès : id={}", id);
    }

    /**
     * Assigne (remplace) les rôles d'un utilisateur.
     *
     * @param userId  UUID de l'utilisateur.
     * @param request Ensemble des UUID de rôles à assigner.
     * @return DTO de l'utilisateur mis à jour.
     */
    @Transactional
    public UserResponse assignRoles(final UUID userId, final AssignRolesRequest request) {
        log.debug(
                "[UserService] Assignation de {} rôle(s) à l'utilisateur id={}",
                request.roleIds().size(), userId);

        final User user = findUserEntityById(userId);

        final Set<Role> roles = request.roleIds().stream()
                .map(roleService::findRoleEntityById)
                .collect(Collectors.toSet());

        user.getRoles().clear();
        user.getRoles().addAll(roles);

        final User saved = userRepository.save(user);

        eventPublisher.publishEvent(NotificationRequestedEvent.of(
                saved.getId(),
                saved.getEmail(),
                null,
                saved.getFirstname() + " " + saved.getLastname(),
                Set.of(NotificationChannel.IN_APP),
                NotificationPriority.NORMAL,
                "ROLE_ASSIGNED",
                Map.of("roles", roles.stream().map(Role::getName).collect(Collectors.joining(", "))),
                "De nouveaux rôles vous ont été assignés"));

        log.debug("[UserService] Rôles assignés avec succès à l'utilisateur id={}", userId);
        return userMapper.toResponse(saved);
    }

    /**
     * Révoque un rôle d'un utilisateur.
     *
     * @param userId UUID de l'utilisateur.
     * @param roleId UUID du rôle à révoquer.
     * @return DTO de l'utilisateur mis à jour.
     */
    @Transactional
    public UserResponse revokeRole(final UUID userId, final UUID roleId) {
        log.debug(
                "[UserService] Révocation du rôle id={} de l'utilisateur id={}",
                roleId, userId);

        final User user = findUserEntityById(userId);
        user.getRoles().removeIf(r -> r.getId().equals(roleId));

        final User saved = userRepository.save(user);

        eventPublisher.publishEvent(NotificationRequestedEvent.of(
                saved.getId(),
                saved.getEmail(),
                null,
                saved.getFirstname() + " " + saved.getLastname(),
                Set.of(NotificationChannel.IN_APP),
                NotificationPriority.NORMAL,
                "ROLE_REVOKED",
                Map.of("roleId", roleId.toString()),
                "Un rôle vous a été retiré"));

        log.debug("[UserService] Rôle révoqué avec succès de l'utilisateur id={}", userId);
        return userMapper.toResponse(saved);
    }

    // ===== Package-private : accessible aux autres services =====

    public User findUserEntityById(final UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Utilisateur introuvable avec l'id : " + id));
    }

    // ===== Méthodes privées =====

    private void assertUsernameAndEmailNotExists(final String username, final String email) {
        if (userRepository.existsByUsername(username)) {
            log.debug("[UserService] Conflit : le username '{}' est déjà pris", username);
            throw new ResourceAlreadyExistsException(
                    "Le nom d'utilisateur '" + username + "' est déjà utilisé");
        }
        if (userRepository.existsByEmail(email)) {
            log.debug("[UserService] Conflit : l'email '{}' est déjà pris", email);
            throw new ResourceAlreadyExistsException(
                    "L'adresse e-mail '" + email + "' est déjà utilisée");
        }
    }
}

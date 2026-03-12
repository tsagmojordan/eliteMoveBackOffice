package com.karibu.ride_app_backend.authentication.service;

import com.karibu.ride_app_backend.authentication.dto.request.AssignPermissionsRequest;
import com.karibu.ride_app_backend.authentication.dto.request.RoleRequest;
import com.karibu.ride_app_backend.authentication.dto.response.RoleResponse;
import com.karibu.ride_app_backend.authentication.mapper.RoleMapper;
import com.karibu.ride_app_backend.authentication.model.Permission;
import com.karibu.ride_app_backend.authentication.model.Role;
import com.karibu.ride_app_backend.authentication.repository.RoleRepository;
import com.karibu.ride_app_backend.authentication.utils.AuthenticationExceptions.ResourceAlreadyExistsException;
import com.karibu.ride_app_backend.authentication.utils.AuthenticationExceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Service métier pour la gestion des rôles.
 *
 * <p>
 * Délègue la résolution des permissions à {@link PermissionService}
 * pour ne pas dupliquer la logique d'accès au repository.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionService permissionService;

    /**
     * Retourne tous les rôles.
     *
     * @return Liste de tous les rôles.
     */
    public List<RoleResponse> findAll() {
        log.debug("[RoleService] Récupération de tous les rôles");
        return roleRepository.findAll()
                .stream()
                .map(roleMapper::toResponse)
                .toList();
    }

    /**
     * Retourne tous les rôles de façon paginée et filtrée.
     */
    public com.karibu.ride_app_backend.shared.dto.PaginatedResponse<RoleResponse> findAll(String search,
            org.springframework.data.domain.Pageable pageable) {
        log.debug("[RoleService] Recherche des rôles avec search={} et page={}", search, pageable.getPageNumber());
        org.springframework.data.domain.Page<Role> pageResult = roleRepository.searchRoles(search, pageable);
        return com.karibu.ride_app_backend.shared.dto.PaginatedResponse.from(pageResult.map(roleMapper::toResponse));
    }

    /**
     * Retourne un rôle par son identifiant.
     *
     * @param id UUID du rôle.
     * @return DTO de réponse.
     */
    public RoleResponse findById(final UUID id) {
        log.debug("[RoleService] Recherche du rôle id={}", id);
        return roleRepository.findById(id)
                .map(roleMapper::toResponse)
                .orElseThrow(() -> {
                    log.debug("[RoleService] Rôle introuvable id={}", id);
                    return new ResourceNotFoundException("Rôle introuvable avec l'id : " + id);
                });
    }

    /**
     * Crée un nouveau rôle.
     *
     * @param request DTO de création.
     * @return DTO du rôle créé.
     */
    @Transactional
    public RoleResponse create(final RoleRequest request) {
        log.debug("[RoleService] Création du rôle : {}", request.name());
        assertRoleNameNotExists(request.name());

        final Role role = roleMapper.toEntity(request);
        final Role saved = roleRepository.save(role);

        log.debug("[RoleService] Rôle créé avec succès : id={}", saved.getId());
        return roleMapper.toResponse(saved);
    }

    /**
     * Met à jour un rôle existant.
     *
     * @param id      UUID du rôle.
     * @param request DTO de mise à jour.
     * @return DTO du rôle mis à jour.
     */
    @Transactional
    public RoleResponse update(final UUID id, final RoleRequest request) {
        log.debug("[RoleService] Mise à jour du rôle id={}", id);
        final Role existing = findRoleEntityById(id);

        if (!existing.getName().equals(request.name())) {
            assertRoleNameNotExists(request.name());
        }

        roleMapper.partialUpdate(existing, request);
        final Role updated = roleRepository.save(existing);

        log.debug("[RoleService] Rôle mis à jour avec succès : id={}", updated.getId());
        return roleMapper.toResponse(updated);
    }

    /**
     * Supprime un rôle.
     *
     * @param id UUID du rôle.
     */
    @Transactional
    public void delete(final UUID id) {
        log.debug("[RoleService] Suppression du rôle id={}", id);
        final Role role = findRoleEntityById(id);
        roleRepository.delete(role);
        log.debug("[RoleService] Rôle supprimé avec succès : id={}", id);
    }

    /**
     * Assigne (remplace) les permissions d'un rôle.
     *
     * @param roleId  UUID du rôle.
     * @param request Ensemble des UUID de permissions à assigner.
     * @return DTO du rôle mis à jour.
     */
    @Transactional
    public RoleResponse assignPermissions(
            final UUID roleId,
            final AssignPermissionsRequest request) {
        log.debug(
                "[RoleService] Assignation de {} permission(s) au rôle id={}",
                request.permissionIds().size(), roleId);

        final Role role = findRoleEntityById(roleId);

        final Set<Permission> permissions = request.permissionIds().stream()
                .map(permissionService::findPermissionEntityById)
                .collect(Collectors.toSet());

        role.getPermissions().clear();
        role.getPermissions().addAll(permissions);

        final Role saved = roleRepository.save(role);
        log.debug("[RoleService] Permissions assignées avec succès au rôle id={}", roleId);
        return roleMapper.toResponse(saved);
    }

    /**
     * Révoque une permission d'un rôle.
     *
     * @param roleId       UUID du rôle.
     * @param permissionId UUID de la permission à révoquer.
     * @return DTO du rôle mis à jour.
     */
    @Transactional
    public RoleResponse revokePermission(final UUID roleId, final UUID permissionId) {
        log.debug(
                "[RoleService] Révocation de la permission id={} du rôle id={}",
                permissionId, roleId);

        final Role role = findRoleEntityById(roleId);
        role.getPermissions().removeIf(p -> p.getId().equals(permissionId));

        final Role saved = roleRepository.save(role);
        log.debug("[RoleService] Permission révoquée avec succès du rôle id={}", roleId);
        return roleMapper.toResponse(saved);
    }

    // ===== Package-private : accessible au UserService =====

    public Role findRoleEntityById(final UUID id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Rôle introuvable avec l'id : " + id));
    }

    // ===== Méthodes privées =====

    private void assertRoleNameNotExists(final String name) {
        if (roleRepository.existsByName(name)) {
            log.debug("[RoleService] Conflit : le rôle '{}' existe déjà", name);
            throw new ResourceAlreadyExistsException(
                    "Un rôle avec le nom '" + name + "' existe déjà");
        }
    }
}

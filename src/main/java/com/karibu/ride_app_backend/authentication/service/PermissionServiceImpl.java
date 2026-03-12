package com.karibu.ride_app_backend.authentication.service;

import com.karibu.ride_app_backend.authentication.dto.request.PermissionRequest;
import com.karibu.ride_app_backend.authentication.dto.response.PermissionResponse;
import com.karibu.ride_app_backend.authentication.mapper.PermissionMapper;
import com.karibu.ride_app_backend.authentication.model.Permission;
import com.karibu.ride_app_backend.authentication.repository.PermissionRepository;
import com.karibu.ride_app_backend.authentication.utils.AuthenticationExceptions.ResourceAlreadyExistsException;
import com.karibu.ride_app_backend.authentication.utils.AuthenticationExceptions.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Service métier pour la gestion des permissions.
 *
 * <p>
 * Respecte le principe SRP : chaque méthode ne fait qu'une seule chose.
 * La logique de validation est déléguée à des helpers privés.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    /**
     * Récupère toutes les permissions paginées.
     *
     * @return Liste des permissions.
     */
    public List<PermissionResponse> findAll() {
        log.debug("[PermissionService] Récupération de toutes les permissions");
        return permissionRepository.findAll()
                .stream()
                .map(permissionMapper::toResponse)
                .toList();
    }

    /**
     * Retourne toutes les permissions de façon paginée et filtrée.
     */
    public com.karibu.ride_app_backend.shared.dto.PaginatedResponse<PermissionResponse> findAll(String search,
            org.springframework.data.domain.Pageable pageable) {
        log.debug("[PermissionService] Recherche des permissions avec search={} et page={}", search,
                pageable.getPageNumber());
        org.springframework.data.domain.Page<Permission> pageResult = permissionRepository.searchPermissions(search,
                pageable);
        return com.karibu.ride_app_backend.shared.dto.PaginatedResponse
                .from(pageResult.map(permissionMapper::toResponse));
    }

    /**
     * Récupère une permission par son identifiant.
     *
     * @param id UUID de la permission.
     * @return DTO de réponse.
     * @throws ResourceNotFoundException si la permission est introuvable.
     */
    public PermissionResponse findById(final UUID id) {
        log.debug("[PermissionService] Recherche de la permission avec id={}", id);
        return permissionRepository.findById(id)
                .map(permissionMapper::toResponse)
                .orElseThrow(() -> {
                    log.debug("[PermissionService] Permission introuvable avec id={}", id);
                    return new ResourceNotFoundException("Permission introuvable avec l'id : " + id);
                });
    }

    /**
     * Crée une nouvelle permission.
     *
     * @param request DTO de création.
     * @return DTO de la permission créée.
     * @throws ResourceAlreadyExistsException si le nom est déjà utilisé.
     */
    @Transactional
    public PermissionResponse create(final PermissionRequest request) {
        log.debug("[PermissionService] Création de la permission : {}", request.name());
        assertPermissionNameNotExists(request.name());

        final Permission permission = permissionMapper.toEntity(request);
        final Permission saved = permissionRepository.save(permission);

        log.debug("[PermissionService] Permission créée avec succès : id={}", saved.getId());
        return permissionMapper.toResponse(saved);
    }

    /**
     * Met à jour une permission existante.
     *
     * @param id      UUID de la permission.
     * @param request DTO de mise à jour.
     * @return DTO de la permission mise à jour.
     */
    @Transactional
    public PermissionResponse update(final UUID id, final PermissionRequest request) {
        log.debug("[PermissionService] Mise à jour de la permission id={}", id);
        final Permission existing = findPermissionEntityById(id);

        if (!existing.getName().equals(request.name())) {
            assertPermissionNameNotExists(request.name());
        }

        permissionMapper.partialUpdate(existing, request);
        final Permission updated = permissionRepository.save(existing);

        log.debug("[PermissionService] Permission mise à jour avec succès : id={}", updated.getId());
        return permissionMapper.toResponse(updated);
    }

    /**
     * Supprime une permission par son identifiant.
     *
     * @param id UUID de la permission.
     */
    @Transactional
    public void delete(final UUID id) {
        log.debug("[PermissionService] Suppression de la permission id={}", id);
        final Permission permission = findPermissionEntityById(id);
        permissionRepository.delete(permission);
        log.debug("[PermissionService] Permission supprimée avec succès : id={}", id);
    }

    // ===== Package-private : accessible au RoleService =====

    /**
     * Récupère une entité {@link Permission} par son id.
     * Exposé en package-private pour éviter les doublons dans RoleService.
     */
    public Permission findPermissionEntityById(final UUID id) {
        return permissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Permission introuvable avec l'id : " + id));
    }

    // ===== Méthodes privées =====

    private void assertPermissionNameNotExists(final String name) {
        if (permissionRepository.existsByName(name)) {
            log.debug("[PermissionService] Conflit : la permission '{}' existe déjà", name);
            throw new ResourceAlreadyExistsException(
                    "Une permission avec le nom '" + name + "' existe déjà");
        }
    }
}

package com.karibu.ride_app_backend.authentication.controller;

import com.karibu.ride_app_backend.authentication.dto.request.AssignPermissionsRequest;
import com.karibu.ride_app_backend.authentication.dto.request.RoleRequest;
import com.karibu.ride_app_backend.authentication.dto.response.RoleResponse;
import com.karibu.ride_app_backend.authentication.service.RoleService;
import com.karibu.ride_app_backend.authentication.utils.ApiResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Contrôleur REST pour la gestion CRUD des rôles
 * et l'assignation/révocation de permissions.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
public class RoleController {

        private final RoleService roleService;

        /**
         * Retourne la liste de tous les rôles.
         *
         * <p>
         * GET /api/v1/roles
         */
        @GetMapping
        @PreAuthorize("hasAuthority('ROLE_READ')")
        public ResponseEntity<ApiResponse<com.karibu.ride_app_backend.shared.dto.PaginatedResponse<RoleResponse>>> findAll(
                        @RequestParam(required = false) final String search,
                        final org.springframework.data.domain.Pageable pageable) {
                log.debug("[RoleController] GET /api/v1/roles avec recherche et pagination");
                return ResponseEntity.ok(
                                ApiResponse.success("Liste paginée des rôles récupérée",
                                                roleService.findAll(search, pageable)));
        }

        /**
         * Retourne un rôle par son identifiant.
         *
         * <p>
         * GET /api/v1/roles/{id}
         */
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('ROLE_READ')")
        public ResponseEntity<ApiResponse<RoleResponse>> findById(@PathVariable final UUID id) {
                log.debug("[RoleController] GET /api/v1/roles/{}", id);
                return ResponseEntity.ok(
                                ApiResponse.success("Rôle trouvé", roleService.findById(id)));
        }

        /**
         * Crée un nouveau rôle.
         *
         * <p>
         * POST /api/v1/roles
         */
        @PostMapping
        @PreAuthorize("hasAuthority('ROLE_CREATE')")
        public ResponseEntity<ApiResponse<RoleResponse>> create(
                        @Valid @RequestBody final RoleRequest request) {
                log.debug("[RoleController] POST /api/v1/roles - name={}", request.name());
                final RoleResponse created = roleService.create(request);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(ApiResponse.created("Rôle créé avec succès", created));
        }

        /**
         * Met à jour un rôle existant.
         *
         * <p>
         * PUT /api/v1/roles/{id}
         */
        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('ROLE_UPDATE')")
        public ResponseEntity<ApiResponse<RoleResponse>> update(
                        @PathVariable final UUID id,
                        @Valid @RequestBody final RoleRequest request) {
                log.debug("[RoleController] PUT /api/v1/roles/{}", id);
                return ResponseEntity.ok(
                                ApiResponse.success("Rôle mis à jour", roleService.update(id, request)));
        }

        /**
         * Supprime un rôle.
         *
         * <p>
         * DELETE /api/v1/roles/{id}
         */
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('ROLE_DELETE')")
        public ResponseEntity<ApiResponse<Void>> delete(@PathVariable final UUID id) {
                log.debug("[RoleController] DELETE /api/v1/roles/{}", id);
                roleService.delete(id);
                return ResponseEntity.ok(ApiResponse.success("Rôle supprimé avec succès"));
        }

        /**
         * Assigne (remplace) les permissions d'un rôle.
         *
         * <p>
         * POST /api/v1/roles/{id}/permissions
         */
        @PostMapping("/{id}/permissions")
        @PreAuthorize("hasAuthority('ROLE_MANAGE_PERMISSIONS')")
        public ResponseEntity<ApiResponse<RoleResponse>> assignPermissions(
                        @PathVariable final UUID id,
                        @Valid @RequestBody final AssignPermissionsRequest request) {
                log.debug(
                                "[RoleController] POST /api/v1/roles/{}/permissions — {} permission(s)",
                                id, request.permissionIds().size());
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Permissions assignées au rôle avec succès",
                                                roleService.assignPermissions(id, request)));
        }

        /**
         * Révoque une permission d'un rôle.
         *
         * <p>
         * DELETE /api/v1/roles/{id}/permissions/{permissionId}
         */
        @DeleteMapping("/{id}/permissions/{permissionId}")
        @PreAuthorize("hasAuthority('ROLE_MANAGE_PERMISSIONS')")
        public ResponseEntity<ApiResponse<RoleResponse>> revokePermission(
                        @PathVariable final UUID id,
                        @PathVariable final UUID permissionId) {
                log.debug(
                                "[RoleController] DELETE /api/v1/roles/{}/permissions/{}",
                                id, permissionId);
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Permission révoquée du rôle avec succès",
                                                roleService.revokePermission(id, permissionId)));
        }
}

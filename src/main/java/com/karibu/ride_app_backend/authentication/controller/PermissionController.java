package com.karibu.ride_app_backend.authentication.controller;

import com.karibu.ride_app_backend.authentication.dto.request.PermissionRequest;
import com.karibu.ride_app_backend.authentication.dto.response.PermissionResponse;
import com.karibu.ride_app_backend.authentication.service.PermissionService;
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
 * Contrôleur REST pour la gestion CRUD des permissions.
 *
 * <p>
 * Toutes les opérations sont protégées par {@code @PreAuthorize}.
 * Seuls les utilisateurs possédant la permission {@code PERMISSION_MANAGE}
 * peuvent accéder à ces endpoints.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/permissions")
@RequiredArgsConstructor
public class PermissionController {

        private final PermissionService permissionService;

        /**
         * Retourne la liste de toutes les permissions.
         *
         * <p>
         * GET /api/v1/permissions
         */
        @GetMapping
        @PreAuthorize("hasAuthority('PERMISSION_READ')")
        public ResponseEntity<ApiResponse<com.karibu.ride_app_backend.shared.dto.PaginatedResponse<PermissionResponse>>> findAll(
                        @RequestParam(required = false) final String search,
                        final org.springframework.data.domain.Pageable pageable) {
                log.debug("[PermissionController] GET /api/v1/permissions avec recherche et pagination");
                return ResponseEntity.ok(
                                ApiResponse.success("Liste paginée des permissions récupérée",
                                                permissionService.findAll(search, pageable)));
        }

        /**
         * Retourne une permission par son identifiant.
         *
         * <p>
         * GET /api/v1/permissions/{id}
         *
         * @param id UUID de la permission.
         */
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('PERMISSION_READ')")
        public ResponseEntity<ApiResponse<PermissionResponse>> findById(
                        @PathVariable final UUID id) {
                log.debug("[PermissionController] GET /api/v1/permissions/{}", id);
                return ResponseEntity.ok(
                                ApiResponse.success("Permission trouvée", permissionService.findById(id)));
        }

        /**
         * Crée une nouvelle permission.
         *
         * <p>
         * POST /api/v1/permissions
         *
         * @param request Corps de la requête validé.
         */
        @PostMapping
        @PreAuthorize("hasAuthority('PERMISSION_CREATE')")
        public ResponseEntity<ApiResponse<PermissionResponse>> create(
                        @Valid @RequestBody final PermissionRequest request) {
                log.debug("[PermissionController] POST /api/v1/permissions - name={}", request.name());
                final PermissionResponse created = permissionService.create(request);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(ApiResponse.created("Permission créée avec succès", created));
        }

        /**
         * Met à jour une permission existante.
         *
         * <p>
         * PUT /api/v1/permissions/{id}
         *
         * @param id      UUID de la permission.
         * @param request Corps de la requête validé.
         */
        @PutMapping("/{id}")
        @PreAuthorize("hasAuthority('PERMISSION_UPDATE')")
        public ResponseEntity<ApiResponse<PermissionResponse>> update(
                        @PathVariable final UUID id,
                        @Valid @RequestBody final PermissionRequest request) {
                log.debug("[PermissionController] PUT /api/v1/permissions/{}", id);
                return ResponseEntity.ok(
                                ApiResponse.success("Permission mise à jour", permissionService.update(id, request)));
        }

        /**
         * Supprime une permission.
         *
         * <p>
         * DELETE /api/v1/permissions/{id}
         *
         * @param id UUID de la permission.
         */
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('PERMISSION_DELETE')")
        public ResponseEntity<ApiResponse<Void>> delete(@PathVariable final UUID id) {
                log.debug("[PermissionController] DELETE /api/v1/permissions/{}", id);
                permissionService.delete(id);
                return ResponseEntity.ok(ApiResponse.success("Permission supprimée avec succès"));
        }
}

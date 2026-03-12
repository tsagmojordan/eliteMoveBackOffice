package com.karibu.ride_app_backend.authentication.controller;

import com.karibu.ride_app_backend.authentication.dto.request.AssignRolesRequest;
import com.karibu.ride_app_backend.authentication.dto.request.CreateUserRequest;
import com.karibu.ride_app_backend.authentication.dto.response.UserResponse;
import com.karibu.ride_app_backend.authentication.service.UserService;
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
 * Contrôleur REST pour la gestion des utilisateurs.
 *
 * <p>
 * Inclut les opérations CRUD, l'activation/désactivation
 * et la gestion des rôles des utilisateurs.
 */
@Slf4j
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

        private final UserService userService;

        /**
         * Retourne la liste de tous les utilisateurs.
         *
         * <p>
         * GET /api/v1/users
         */
        @GetMapping
        @PreAuthorize("hasAuthority('USER_READ')")
        public ResponseEntity<ApiResponse<com.karibu.ride_app_backend.shared.dto.PaginatedResponse<UserResponse>>> findAll(
                        @RequestParam(required = false) final String search,
                        final org.springframework.data.domain.Pageable pageable) {
                log.debug("[UserController] GET /api/v1/users avec recherche et pagination");
                return ResponseEntity.ok(
                                ApiResponse.success("Liste paginée des utilisateurs récupérée",
                                                userService.findAll(search, pageable)));
        }

        /**
         * Retourne un utilisateur par son identifiant.
         *
         * <p>
         * GET /api/v1/users/{id}
         */
        @GetMapping("/{id}")
        @PreAuthorize("hasAuthority('USER_READ')")
        public ResponseEntity<ApiResponse<UserResponse>> findById(@PathVariable final UUID id) {
                log.debug("[UserController] GET /api/v1/users/{}", id);
                return ResponseEntity.ok(
                                ApiResponse.success("Utilisateur trouvé", userService.findById(id)));
        }

        /**
         * Crée un nouvel utilisateur.
         *
         * <p>
         * POST /api/v1/users
         */
        @PostMapping
        @PreAuthorize("hasAuthority('USER_CREATE')")
        public ResponseEntity<ApiResponse<UserResponse>> create(
                        @Valid @RequestBody final CreateUserRequest request) {
                log.debug("[UserController] POST /api/v1/users - username={}", request.username());
                final UserResponse created = userService.create(request);
                return ResponseEntity
                                .status(HttpStatus.CREATED)
                                .body(ApiResponse.created("Utilisateur créé avec succès", created));
        }

        /**
         * Active ou désactive le compte d'un utilisateur.
         *
         * <p>
         * PATCH /api/v1/users/{id}/status?enabled=true|false
         */
        @PatchMapping("/{id}/status")
        @PreAuthorize("hasAuthority('USER_UPDATE')")
        public ResponseEntity<ApiResponse<UserResponse>> setEnabled(
                        @PathVariable final UUID id,
                        @RequestParam final boolean enabled) {
                log.debug("[UserController] PATCH /api/v1/users/{}/status?enabled={}", id, enabled);
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                enabled ? "Compte activé avec succès" : "Compte désactivé avec succès",
                                                userService.setEnabled(id, enabled)));
        }

        /**
         * Supprime un utilisateur.
         *
         * <p>
         * DELETE /api/v1/users/{id}
         */
        @DeleteMapping("/{id}")
        @PreAuthorize("hasAuthority('USER_DELETE')")
        public ResponseEntity<ApiResponse<Void>> delete(@PathVariable final UUID id) {
                log.debug("[UserController] DELETE /api/v1/users/{}", id);
                userService.delete(id);
                return ResponseEntity.ok(ApiResponse.success("Utilisateur supprimé avec succès"));
        }

        /**
         * Assigne (remplace) les rôles d'un utilisateur.
         *
         * <p>
         * POST /api/v1/users/{id}/roles
         */
        @PostMapping("/{id}/roles")
        @PreAuthorize("hasAuthority('USER_MANAGE_ROLES')")
        public ResponseEntity<ApiResponse<UserResponse>> assignRoles(
                        @PathVariable final UUID id,
                        @Valid @RequestBody final AssignRolesRequest request) {
                log.debug(
                                "[UserController] POST /api/v1/users/{}/roles — {} rôle(s)",
                                id, request.roleIds().size());
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Rôles assignés à l'utilisateur avec succès",
                                                userService.assignRoles(id, request)));
        }

        /**
         * Révoque un rôle d'un utilisateur.
         *
         * <p>
         * DELETE /api/v1/users/{id}/roles/{roleId}
         */
        @DeleteMapping("/{id}/roles/{roleId}")
        @PreAuthorize("hasAuthority('USER_MANAGE_ROLES')")
        public ResponseEntity<ApiResponse<UserResponse>> revokeRole(
                        @PathVariable final UUID id,
                        @PathVariable final UUID roleId) {
                log.debug(
                                "[UserController] DELETE /api/v1/users/{}/roles/{}",
                                id, roleId);
                return ResponseEntity.ok(
                                ApiResponse.success(
                                                "Rôle révoqué de l'utilisateur avec succès",
                                                userService.revokeRole(id, roleId)));
        }
}

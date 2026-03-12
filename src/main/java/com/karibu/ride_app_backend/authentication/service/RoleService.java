package com.karibu.ride_app_backend.authentication.service;

import com.karibu.ride_app_backend.authentication.dto.request.AssignPermissionsRequest;
import com.karibu.ride_app_backend.authentication.dto.request.RoleRequest;
import com.karibu.ride_app_backend.authentication.dto.response.RoleResponse;

import java.util.List;
import java.util.UUID;

/**
 * Interface du service métier pour la gestion des rôles.
 */
public interface RoleService {

    List<RoleResponse> findAll();

    com.karibu.ride_app_backend.shared.dto.PaginatedResponse<RoleResponse> findAll(String search,
            org.springframework.data.domain.Pageable pageable);

    RoleResponse findById(UUID id);

    RoleResponse create(RoleRequest request);

    RoleResponse update(UUID id, RoleRequest request);

    void delete(UUID id);

    RoleResponse assignPermissions(UUID roleId, AssignPermissionsRequest request);

    RoleResponse revokePermission(UUID roleId, UUID permissionId);

    com.karibu.ride_app_backend.authentication.model.Role findRoleEntityById(UUID id);
}

package com.karibu.ride_app_backend.authentication.service;

import com.karibu.ride_app_backend.authentication.dto.request.PermissionRequest;
import com.karibu.ride_app_backend.authentication.dto.response.PermissionResponse;

import java.util.List;
import java.util.UUID;

/**
 * Interface du service métier pour la gestion des permissions.
 */
public interface PermissionService {

    List<PermissionResponse> findAll();

    com.karibu.ride_app_backend.shared.dto.PaginatedResponse<PermissionResponse> findAll(String search,
            org.springframework.data.domain.Pageable pageable);

    PermissionResponse findById(UUID id);

    PermissionResponse create(PermissionRequest request);

    PermissionResponse update(UUID id, PermissionRequest request);

    void delete(UUID id);

    com.karibu.ride_app_backend.authentication.model.Permission findPermissionEntityById(UUID id);
}

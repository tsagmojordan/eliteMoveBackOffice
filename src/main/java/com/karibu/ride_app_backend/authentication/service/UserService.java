package com.karibu.ride_app_backend.authentication.service;

import com.karibu.ride_app_backend.authentication.dto.request.AssignRolesRequest;
import com.karibu.ride_app_backend.authentication.dto.request.CreateUserRequest;
import com.karibu.ride_app_backend.authentication.dto.response.UserResponse;

import java.util.List;
import java.util.UUID;

/**
 * Interface du service métier pour la gestion des utilisateurs.
 */
public interface UserService {

    List<UserResponse> findAll();
    
    com.karibu.ride_app_backend.shared.dto.PaginatedResponse<UserResponse> findAll(String search, org.springframework.data.domain.Pageable pageable);

    UserResponse findById(UUID id);

    UserResponse create(CreateUserRequest request);

    UserResponse setEnabled(UUID id, boolean enabled);

    void delete(UUID id);

    UserResponse assignRoles(UUID userId, AssignRolesRequest request);

    UserResponse revokeRole(UUID userId, UUID roleId);

    com.karibu.ride_app_backend.authentication.model.User findUserEntityById(UUID id);
}

package com.karibu.ride_app_backend.authentication.repository;


import com.karibu.ride_app_backend.authentication.model.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour {@link Permission}.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, UUID> {

    Optional<Permission> findByName(String name);

    boolean existsByName(String name);

    @org.springframework.data.jpa.repository.Query("SELECT p FROM Permission p WHERE " +
            ":search IS NULL OR " +
            "LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :search, '%'))")
    org.springframework.data.domain.Page<Permission> searchPermissions(
            @org.springframework.data.repository.query.Param("search") String search,
            org.springframework.data.domain.Pageable pageable);
}

package com.karibu.ride_app_backend.authentication.repository;


import com.karibu.ride_app_backend.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour {@link User}.
 */
@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    /** Vérifie existence par username OU email (utile à la création). */
    @Query("SELECT COUNT(u) > 0 FROM User u WHERE u.username = :username OR u.email = :email")
    boolean existsByUsernameOrEmail(String username, String email);

    @Query("SELECT u FROM User u WHERE " +
            ":search IS NULL OR " +
            "LOWER(u.username) LIKE LOWER(CONCAT('%', CAST(:search AS STRING), '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', CAST(:search AS STRING), '%')) OR " +
            "LOWER(u.firstname) LIKE LOWER(CONCAT('%', CAST(:search AS STRING), '%')) OR " +
            "LOWER(u.lastname) LIKE LOWER(CONCAT('%', CAST(:search AS STRING), '%'))")
    org.springframework.data.domain.Page<User> searchUsers(
            @Param("search") String search,
            org.springframework.data.domain.Pageable pageable);

    /**
     * Récupère tous les users ayant un rôle spécifique.
     *
     * @param roleName le nom du rôle (ex: "ADMIN", "ROLE_ADMIN")
     * @return liste des users ayant ce rôle
     */
    @Query("SELECT u FROM User u " +
            "JOIN u.roles r " +
            "WHERE r.name = :roleName")
    List<User> findByRoleName(@Param("roleName") String roleName);
}

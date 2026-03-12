package com.karibu.ride_app_backend.authentication.repository;


import com.karibu.ride_app_backend.authentication.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

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
            "LOWER(u.username) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.email) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.firstname) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "LOWER(u.lastname) LIKE LOWER(CONCAT('%', :search, '%'))")
    org.springframework.data.domain.Page<User> searchUsers(
            @org.springframework.data.repository.query.Param("search") String search,
            org.springframework.data.domain.Pageable pageable);
}

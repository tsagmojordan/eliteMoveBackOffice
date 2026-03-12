package com.karibu.ride_app_backend.authentication.repository;


import com.karibu.ride_app_backend.authentication.model.Token;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour {@link Token}.
 */
@Repository
public interface TokenRepository extends JpaRepository<Token, UUID> {

    /**
     * Retourne tous les tokens valides (non révoqués et non expirés) d'un
     * utilisateur.
     * Utilisé lors de la rotation des tokens pour révoquer les anciens.
     */
    @Query("""
                SELECT t FROM Token t
                WHERE t.user.id = :userId
                  AND t.revoked = false
                  AND t.expired = false
            """)
    List<Token> findAllValidTokensByUserId(UUID userId);

    Optional<Token> findByTokenValue(String tokenValue);
}

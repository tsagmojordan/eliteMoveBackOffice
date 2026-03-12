package com.karibu.ride_app_backend.call.infrastructure.persistence;

import com.karibu.ride_app_backend.call.domain.model.Call;
import com.karibu.ride_app_backend.call.domain.model.CallStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

/**
 * Repository Spring Data JPA pour l'entité {@link Call}.
 *
 * <p>
 * Interface interne à l'infrastructure. Elle NE doit PAS être exposée
 * directement dans le domaine ou l'application. L'adaptateur
 * {@link CallRepositoryAdapter}
 * fait le pont entre cette interface Spring Data et le port domaine.
 */
public interface JpaCallRepository extends JpaRepository<Call, UUID> {

    /**
     * Récupère tous les appels où l'utilisateur est soit appelant soit
     * destinataire.
     * Triés par date de création décroissante.
     *
     * @param userId Identifiant de l'utilisateur.
     * @return Liste des appels.
     */
    @Query("SELECT c FROM Call c WHERE c.callerId = :userId OR c.calleeId = :userId ORDER BY c.createdAt DESC")
    List<Call> findAllByUserId(@Param("userId") UUID userId);

    /**
     * Version paginée de la requête précédente.
     *
     * @param userId   Identifiant de l'utilisateur.
     * @param pageable Paramètres de pagination.
     * @return Liste paginée des appels.
     */
    @Query("SELECT c FROM Call c WHERE c.callerId = :userId OR c.calleeId = :userId ORDER BY c.createdAt DESC")
    List<Call> findPageByUserId(@Param("userId") UUID userId, Pageable pageable);

    /**
     * Compte les appels manqués pour un destinataire donné.
     *
     * @param calleeId Identifiant du destinataire.
     * @param status   Statut cible (MISSED).
     * @return Nombre d'appels.
     */
    long countByCalleeIdAndStatus(UUID calleeId, CallStatus status);

    /**
     * Trouve tous les appels dans un statut précis (ex: RINGING bloqués).
     *
     * @param status Statut recherché.
     * @return Liste des appels.
     */
    List<Call> findAllByStatus(CallStatus status);
}

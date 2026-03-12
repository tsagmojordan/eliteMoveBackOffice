package com.karibu.ride_app_backend.call.domain.port.out;

import com.karibu.ride_app_backend.call.domain.model.Call;
import com.karibu.ride_app_backend.call.domain.model.CallStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Port de sortie (Port Out) — Persistence des appels.
 *
 * <p>
 * Interface du domaine implémentée par l'adaptateur JPA dans l'infrastructure.
 * Le domaine n'a aucune dépendance sur JPA, Hibernate ou tout autre framework.
 */
public interface CallRepository {

    /**
     * Sauvegarde (création ou mise à jour) d'un appel.
     *
     * @param call L'appel à persister.
     * @return L'appel persisté (avec ID généré si création).
     */
    Call save(Call call);

    /**
     * Recherche un appel par son identifiant.
     *
     * @param id Identifiant de l'appel.
     * @return Un Optional contenant l'appel s'il existe.
     */
    Optional<Call> findById(UUID id);

    /**
     * Retourne tous les appels impliquant un utilisateur donné
     * (en tant qu'appelant ou destinataire), triés par date décroissante.
     *
     * @param userId Identifiant de l'utilisateur.
     * @return Liste des appels de l'historique.
     */
    List<Call> findAllByUserId(UUID userId);

    /**
     * Retourne l'historique paginé des appels d'un utilisateur.
     *
     * @param userId Identifiant de l'utilisateur.
     * @param page   Numéro de page (0-based).
     * @param size   Nombre d'éléments par page.
     * @return Liste paginée des appels.
     */
    List<Call> findPageByUserId(UUID userId, int page, int size);

    /**
     * Retourne tous les appels dans un statut donné.
     * Utile pour le nettoyage des appels bloqués (ex: RINGING sans réponse).
     *
     * @param status Statut cible.
     * @return Liste des appels correspondants.
     */
    List<Call> findAllByStatus(CallStatus status);

    /**
     * Compte le nombre d'appels manqués non encore vus par un destinataire.
     *
     * @param calleeId Identifiant du destinataire.
     * @return Nombre d'appels manqués.
     */
    long countMissedCallsByCalleeId(UUID calleeId);
}

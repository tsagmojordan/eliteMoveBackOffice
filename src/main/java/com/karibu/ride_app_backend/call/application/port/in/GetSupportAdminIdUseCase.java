package com.karibu.ride_app_backend.call.application.port.in;

import java.util.UUID;

/**
 * Port d'entrée — Résoudre l'administrateur de support à appeler.
 *
 * <p>
 * Utilisé par un client qui demande le support : retourne l'identifiant
 * d'UN administrateur disponible, sans exposer la liste des comptes.
 * La stratégie de sélection (tirage aléatoire aujourd'hui, load balancer
 * demain) est interne au backend — ce contrat ne changera pas.
 */
public interface GetSupportAdminIdUseCase {

    /**
     * Sélectionne un administrateur de support disponible.
     *
     * @return L'identifiant de l'administrateur sélectionné.
     * @throws com.karibu.ride_app_backend.call.domain.exception.NoSupportAdminAvailableException
     *         Si aucun administrateur activé n'existe ou s'ils sont tous
     *         actuellement en appel.
     */
    UUID handle();
}
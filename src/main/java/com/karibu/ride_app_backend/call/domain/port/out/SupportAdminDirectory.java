package com.karibu.ride_app_backend.call.domain.port.out;

import java.util.List;
import java.util.UUID;

/**
 * Port de sortie (Port Out) — Annuaire des administrateurs de support.
 *
 * <p>
 * Abstraction de l'accès aux utilisateurs « support » (rôle ADMIN ou
 * SUPER_ADMIN) détenus par le module d'authentification. Le module Call
 * n'a aucune dépendance directe sur ce module : seul ce port est connu
 * du domaine et de l'application.
 */
public interface SupportAdminDirectory {

    /**
     * Retourne les identifiants des administrateurs de support activés.
     *
     * <p>
     * Un administrateur est « activé » si son compte est {@code enabled}.
     * Les statuts verrouillés/expirés éventuels relèvent de
     * l'authentification et sont filtrés par l'adaptateur.
     *
     * @return Liste (potentiellement vide) d'identifiants d'administrateurs.
     */
    List<UUID> findEnabledSupportAdminIds();
}
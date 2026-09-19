package com.karibu.ride_app_backend.ride.domain.port.out;

import java.util.UUID;

/**
 * Port de sortie — Accès en lecture aux informations d'un véhicule.
 *
 * <p>
 * Utilisé pour calculer le tarif d'une course à partir de la classe du
 * véhicule, sans coupler le domaine des courses au module véhicules.
 */
public interface VehiculeInfoPort {

    /**
     * Retourne la classe du véhicule ({@code ECO}, {@code CONFORT},
     * {@code PREMIUM}, {@code VAN}).
     *
     * @param vehiculeId Identifiant du véhicule (peut être {@code null}).
     * @return Le nom de la classe, ou {@code null} si le véhicule n'existe pas
     *         ou si l'identifiant est {@code null}.
     */
    String findVehiculeClass(UUID vehiculeId);
}
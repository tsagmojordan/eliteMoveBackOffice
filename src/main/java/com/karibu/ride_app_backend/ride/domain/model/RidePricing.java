package com.karibu.ride_app_backend.ride.domain.model;

/**
 * Règle tarifaire des courses — simple et déterministe.
 *
 * <p>
 * Tarif de base selon la classe du véhicule (en FCFA par course) :
 * <ul>
 * <li>{@code ECO} : 500</li>
 * <li>{@code CONFORT} : 1000</li>
 * <li>{@code PREMIUM} : 2000</li>
 * <li>{@code VAN} : 3000</li>
 * </ul>
 *
 * <p>
 * Si la course n'a pas de véhicule ou que la classe est inconnue, le tarif
 * {@code ECO} s'applique par défaut. Le prix est calculé une seule fois, au
 * moment de l'acceptation de la course, puis persisté sur l'entité
 * {@link Ride} pour rester stable même si les tarifs changent.
 *
 * <p>
 * Valeurs à ajuster avec le client le cas échéant.
 */
public final class RidePricing {

    private RidePricing() {
    }

    /**
     * Tarif de base pour une classe de véhicule.
     *
     * @param vehiculeClass Nom de la classe ({@code ECO}, {@code CONFORT},
     *                      {@code PREMIUM}, {@code VAN}) ou {@code null}.
     * @return Le tarif en FCFA.
     */
    public static double baseTariff(final String vehiculeClass) {
        return switch (vehiculeClass == null ? "" : vehiculeClass) {
            case "CONFORT" -> 1000.0;
            case "PREMIUM" -> 2000.0;
            case "VAN" -> 3000.0;
            default -> 500.0; // ECO et valeur par défaut
        };
    }
}
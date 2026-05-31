package com.karibu.ride_app_backend.vehicule.domain.model;

import java.util.Map;
import java.util.Set;

public enum VehiculeStatus {
    AVAILABLE,
    IN_RIDE,
    MAINTENANCE,
    OUT_OF_SERVICE;

    private static final Map<VehiculeStatus, Set<VehiculeStatus>> ALLOWED_TRANSITIONS = Map.of(
            AVAILABLE,       Set.of(IN_RIDE, MAINTENANCE, OUT_OF_SERVICE),
            IN_RIDE,         Set.of(AVAILABLE),
            MAINTENANCE,     Set.of(AVAILABLE, OUT_OF_SERVICE),
            OUT_OF_SERVICE,  Set.of(MAINTENANCE)
    );

    public boolean canTransitionTo(VehiculeStatus next) {
        return ALLOWED_TRANSITIONS.getOrDefault(this, Set.of()).contains(next);
    }
}

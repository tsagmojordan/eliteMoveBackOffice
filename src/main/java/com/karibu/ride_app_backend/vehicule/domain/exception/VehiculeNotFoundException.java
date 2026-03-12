package com.karibu.ride_app_backend.vehicule.domain.exception;

import java.util.UUID;

public class VehiculeNotFoundException extends RuntimeException {
    public VehiculeNotFoundException(UUID id) {
        super("Véhicule non trouvé avec l'id : " + id);
    }
}

package com.karibu.ride_app_backend.ride.domain.exception;

import java.util.UUID;

public class RideNotFoundException extends RuntimeException {
    public RideNotFoundException(UUID id) {
        super("Course non trouvée avec l'id : " + id);
    }
}

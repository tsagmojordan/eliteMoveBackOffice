package com.karibu.ride_app_backend.shared.valueobject;

public record RideCreatedEvent(
        String rideId,
        String requesterId,
        String rideStatus,
        String vehiculeId
) {
    // constructeur canonique: validation des non-null
    public RideCreatedEvent {
        if (rideId == null) throw new InvalidRideCreatedEventException("rideId must not be null");
        if (requesterId == null) throw new InvalidRideCreatedEventException("requesterId must not be null");
        if (rideStatus == null) throw new InvalidRideCreatedEventException("rideStatus must not be null");
        if (vehiculeId == null) throw new InvalidRideCreatedEventException("vehiculeId must not be null");
    }

    // exception imbriquée spécifique au record
    public static class InvalidRideCreatedEventException extends IllegalArgumentException {
        public InvalidRideCreatedEventException(String message) {
            super(message);
        }

        public InvalidRideCreatedEventException(String message, Throwable cause) {

            super(message, cause);
        }
    }
}

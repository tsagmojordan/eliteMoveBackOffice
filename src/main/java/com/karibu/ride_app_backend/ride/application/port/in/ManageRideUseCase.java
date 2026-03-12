package com.karibu.ride_app_backend.ride.application.port.in;

import com.karibu.ride_app_backend.ride.domain.model.Ride;

import java.util.List;
import java.util.UUID;

public interface ManageRideUseCase {
    Ride requestRide(Ride ride);

    Ride getRide(UUID id);

    List<Ride> getAllRides();

    List<Ride> getRidesByUserId(UUID userId);

    Ride updateRideStatus(UUID id, String statusStr);
}

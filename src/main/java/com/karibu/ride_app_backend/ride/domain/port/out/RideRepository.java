package com.karibu.ride_app_backend.ride.domain.port.out;

import com.karibu.ride_app_backend.ride.domain.model.Ride;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RideRepository {
    Ride save(Ride ride);

    Optional<Ride> findById(UUID id);

    List<Ride> findAll();

    List<Ride> findByUserId(UUID userId);
}

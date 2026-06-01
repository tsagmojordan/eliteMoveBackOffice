package com.karibu.ride_app_backend.ride.domain.port.out;

import com.karibu.ride_app_backend.ride.domain.model.Ride;

public interface EventPublisherPort {
    void publishRideCreated(Ride savedRide);
}

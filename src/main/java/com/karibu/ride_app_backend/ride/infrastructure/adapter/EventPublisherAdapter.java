package com.karibu.ride_app_backend.ride.infrastructure.adapter;

import com.karibu.ride_app_backend.ride.domain.model.Ride;
import com.karibu.ride_app_backend.ride.domain.port.out.EventPublisherPort;
import com.karibu.ride_app_backend.shared.valueobject.RideCreatedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class EventPublisherAdapter implements EventPublisherPort {

    private final ApplicationEventPublisher publisher;

    @Override
    public void publishRideCreated(Ride savedRide) {
        log.debug("""
                🚀 [EVENT PUBLISHED] Ride Created
            - Ride ID: {}
            - User ID: {}
            - Status: {}
            """, savedRide.getId(), savedRide.getUserId(), savedRide.getStatus());

        publisher.publishEvent(new RideCreatedEvent(
                savedRide.getId() != null ? savedRide.getId().toString() : "",
                savedRide.getUserId() != null ? savedRide.getUserId().toString() : "",
                savedRide.getStatus() != null ? savedRide.getStatus().name() : "",
                savedRide.getVehiculeId() != null ? savedRide.getVehiculeId().toString() : ""
        ));

    }
}

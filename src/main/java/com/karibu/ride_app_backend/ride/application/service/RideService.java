package com.karibu.ride_app_backend.ride.application.service;

import com.karibu.ride_app_backend.ride.application.port.in.ManageRideUseCase;
import com.karibu.ride_app_backend.ride.domain.exception.RideNotFoundException;
import com.karibu.ride_app_backend.ride.domain.model.Ride;
import com.karibu.ride_app_backend.ride.domain.model.RideStatus;
import com.karibu.ride_app_backend.ride.domain.port.out.NotificationPort;
import com.karibu.ride_app_backend.ride.domain.port.out.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RideService implements ManageRideUseCase {

    private final RideRepository rideRepository;
    private final NotificationPort notificationPort;

    @Override
    @Transactional
    public Ride requestRide(Ride ride) {
        ride.setStatus(RideStatus.REQUESTED);
        ride.setRequestedAt(LocalDateTime.now());
        Ride savedRide = rideRepository.save(ride);

        notificationPort.notifyAdmin("Nouvelle demande de course", savedRide.getId());

        return savedRide;
    }

    @Override
    @Transactional(readOnly = true)
    public Ride getRide(UUID id) {
        return rideRepository.findById(id)
                .orElseThrow(() -> new RideNotFoundException(id));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ride> getAllRides() {
        return rideRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Ride> getRidesByUserId(UUID userId) {
        return rideRepository.findByUserId(userId);
    }

    @Override
    @Transactional
    public Ride updateRideStatus(UUID id, String statusStr) {
        Ride ride = getRide(id);
        RideStatus status = RideStatus.valueOf(statusStr.toUpperCase());

        switch (status) {
            case ACCEPTED -> ride.accept();
            case IN_PROGRESS -> ride.start();
            case COMPLETED -> ride.complete();
            case CANCELLED -> ride.cancel();
            default -> ride.setStatus(status);
        }

        Ride savedRide = rideRepository.save(ride);

        notificationPort.notifyAdmin("Statut de la course mis à jour: " + statusStr, savedRide.getId());

        return savedRide;
    }
}

package com.karibu.ride_app_backend.ride.infrastructure.persistence;

import com.karibu.ride_app_backend.ride.domain.model.Ride;
import com.karibu.ride_app_backend.ride.domain.port.out.RideRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RideRepositoryAdapter implements RideRepository {

    private final JpaRideRepository jpaRideRepository;

    @Override
    public Ride save(Ride ride) {
        JpaRideEntity entity = JpaRideEntity.builder()
                .id(ride.getId())
                .vehiculeId(ride.getVehiculeId())
                .userId(ride.getUserId())
                .pickupLocation(ride.getPickupLocation())
                .dropoffLocation(ride.getDropoffLocation())
                .status(ride.getStatus())
                .requestedAt(ride.getRequestedAt())
                .completedAt(ride.getCompletedAt())
                .build();
        JpaRideEntity saved = jpaRideRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public Optional<Ride> findById(UUID id) {
        return jpaRideRepository.findById(id).map(this::toDomain);
    }

    @Override
    public List<Ride> findAll() {
        return jpaRideRepository.findAll().stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ride> findByUserId(UUID userId) {
        return jpaRideRepository.findByUserId(userId).stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private Ride toDomain(JpaRideEntity entity) {
        return Ride.builder()
                .id(entity.getId())
                .vehiculeId(entity.getVehiculeId())
                .userId(entity.getUserId())
                .pickupLocation(entity.getPickupLocation())
                .dropoffLocation(entity.getDropoffLocation())
                .status(entity.getStatus())
                .requestedAt(entity.getRequestedAt())
                .completedAt(entity.getCompletedAt())
                .build();
    }
}

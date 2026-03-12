package com.karibu.ride_app_backend.ride.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaRideRepository extends JpaRepository<JpaRideEntity, UUID> {
    List<JpaRideEntity> findByUserId(UUID userId);
}

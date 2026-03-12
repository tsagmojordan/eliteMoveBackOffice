package com.karibu.ride_app_backend.vehicule.infrastructure.persistence;

import com.karibu.ride_app_backend.vehicule.domain.model.VehiculeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface JpaVehiculeRepository extends JpaRepository<JpaVehiculeEntity, UUID> {
    List<JpaVehiculeEntity> findByStatus(VehiculeStatus status);
}

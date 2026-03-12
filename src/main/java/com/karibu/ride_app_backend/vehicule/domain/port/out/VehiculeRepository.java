package com.karibu.ride_app_backend.vehicule.domain.port.out;

import com.karibu.ride_app_backend.vehicule.domain.model.Vehicule;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface VehiculeRepository {
    Vehicule save(Vehicule vehicule);

    Optional<Vehicule> findById(UUID id);

    List<Vehicule> findAll();

    List<Vehicule> findAllAvailable();

    void deleteById(UUID id);
}

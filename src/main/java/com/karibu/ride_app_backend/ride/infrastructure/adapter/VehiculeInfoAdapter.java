package com.karibu.ride_app_backend.ride.infrastructure.adapter;

import com.karibu.ride_app_backend.ride.domain.port.out.VehiculeInfoPort;
import com.karibu.ride_app_backend.vehicule.application.port.in.ManageVehiculeUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.UUID;

/**
 * Adaptateur — Fournit au module ride les informations du module vehicule
 * via son port applicatif {@link ManageVehiculeUseCase}.
 */
@Component
@RequiredArgsConstructor
public class VehiculeInfoAdapter implements VehiculeInfoPort {

    private final ManageVehiculeUseCase manageVehiculeUseCase;

    @Override
    public String findVehiculeClass(final UUID vehiculeId) {
        if (vehiculeId == null) {
            return null;
        }
        try {
            return manageVehiculeUseCase.getVehicule(vehiculeId)
                    .getVehiculeClass()
                    .name();
        } catch (final Exception ex) {
            // Véhicule introuvable (supprimé ?) — le tarif par défaut s'appliquera
            return null;
        }
    }
}
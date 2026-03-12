package com.karibu.ride_app_backend.vehicule.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Vehicule {

    private UUID id;
    private String brand;
    private String model;
    private int year;
    private String licensePlate;
    private VehiculeClass vehiculeClass;
    private VehiculeStatus status;

    public void markAsAvailable() {
        this.status = VehiculeStatus.AVAILABLE;
    }

    public void markAsInRide() {
        this.status = VehiculeStatus.IN_RIDE;
    }

    public void markAsMaintenance() {
        this.status = VehiculeStatus.MAINTENANCE;
    }

    public boolean isAvailable() {
        return this.status == VehiculeStatus.AVAILABLE;
    }
}

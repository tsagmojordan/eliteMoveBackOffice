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
    private String photo1;
    private String photo2;
    private String photo3;
    private String licensePlate;
    private VehiculeClass vehiculeClass;
    private VehiculeStatus status;
    private int price;

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

    public void addPrincipalImage(String principalPath) {
        if (principalPath == null) throw new RuntimeException("Vous devez renseigner au moins une photo principale");
        this.photo1 = principalPath;
    }

    public void addSecondImage(String path2) {this.photo2 = path2;}


    public void addSThridImage(String path3)  {this.photo3 = path3;}
}

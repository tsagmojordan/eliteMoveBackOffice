package com.karibu.ride_app_backend.vehicule.domain.model;

import com.karibu.ride_app_backend.vehicule.domain.model.value_object.VehiculeLocation;
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
    private String photo1MimeType;
    private String photo2MimeType;
    private String photo3MimeType;
    private String photo1ThumbnailPath;
    private String licensePlate;
    private VehiculeLocation location;
    private VehiculeClass vehiculeClass;
    private VehiculeStatus status;
    private int price;

    private void transitionTo(VehiculeStatus next) {
        if (!this.status.canTransitionTo(next)) {
            throw new VehiculeException(String.format(
                    "Transition invalide : %s → %s", this.status, next
            ));
        }
        this.status = next;
    }

    public void markAsAvailable() {
        transitionTo(VehiculeStatus.AVAILABLE);
    }

    public void markAsInRide() {
        transitionTo(VehiculeStatus.IN_RIDE);
    }

    public void markAsMaintenance() {
        transitionTo(VehiculeStatus.MAINTENANCE);
    }

    public void markAsOutOfService() {
        transitionTo(VehiculeStatus.OUT_OF_SERVICE);
    }

    public boolean isAvailable() {
        return this.status == VehiculeStatus.AVAILABLE;
    }

    public void addPrincipalImage(String principalPath) {
        if (principalPath == null) throw new RuntimeException("Vous devez renseigner au moins une photo principale");
        this.photo1 = principalPath;
    }

    public void addSecondImage(String path2) {
        this.photo2 = path2;
    }

    public void addSThridImage(String path3) {
        this.photo3 = path3;
    }

    public void defineLocation(VehiculeLocation vehiculeLocation) {
        this.location = vehiculeLocation;
    }

    public static class VehiculeException extends RuntimeException {
        public VehiculeException(String message) {
            super(message);
        }
    }
}

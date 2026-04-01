package com.karibu.ride_app_backend.vehicule.api.dto;

import com.karibu.ride_app_backend.vehicule.domain.model.VehiculeClass;
import com.karibu.ride_app_backend.vehicule.domain.model.VehiculeStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class VehiculeDto {
    private UUID id;
    private String brand;
    private String model;
    private int year;
    private String licensePlate;
    private VehiculeClass vehiculeClass;
    private VehiculeStatus status;
    private String longitude;
    private String latitude;
    private int price;
}

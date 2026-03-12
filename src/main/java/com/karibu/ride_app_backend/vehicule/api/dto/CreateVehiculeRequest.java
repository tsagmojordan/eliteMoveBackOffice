package com.karibu.ride_app_backend.vehicule.api.dto;

import com.karibu.ride_app_backend.vehicule.domain.model.VehiculeClass;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateVehiculeRequest {
    @NotBlank
    private String brand;
    @NotBlank
    private String model;
    @NotNull
    private Integer year;
    @NotBlank
    private String licensePlate;
    @NotNull
    private VehiculeClass vehiculeClass;
}

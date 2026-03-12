package com.karibu.ride_app_backend.ride.api.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class CreateRideRequest {
    @NotNull
    private UUID userId;
    private UUID vehiculeId; // Peut être null si on assigne le véhicule plus tard
    @NotBlank
    private String pickupLocation;
    @NotBlank
    private String dropoffLocation;
}

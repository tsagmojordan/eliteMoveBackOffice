package com.karibu.ride_app_backend.ride.api.dto;

import com.karibu.ride_app_backend.ride.domain.model.RideStatus;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class RideDto {
    private UUID id;
    private UUID vehiculeId;
    private UUID userId;
    private String pickupLocation;
    private String dropoffLocation;
    private RideStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;
}

package com.karibu.ride_app_backend.ride.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ride {

    private UUID id;
    private UUID vehiculeId;
    private UUID userId;
    private String pickupLocation;
    private String dropoffLocation;
    private RideStatus status;
    private LocalDateTime requestedAt;
    private LocalDateTime completedAt;

    public void accept() {
        this.status = RideStatus.ACCEPTED;
    }

    public void start() {
        this.status = RideStatus.IN_PROGRESS;
    }

    public void complete() {
        this.status = RideStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void cancel() {
        this.status = RideStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
    }
}

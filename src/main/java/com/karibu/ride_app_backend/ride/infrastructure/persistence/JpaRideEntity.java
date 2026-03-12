package com.karibu.ride_app_backend.ride.infrastructure.persistence;

import com.karibu.ride_app_backend.ride.domain.model.RideStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "rides")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class JpaRideEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "vehicule_id")
    private UUID vehiculeId;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "pickup_location", nullable = false)
    private String pickupLocation;

    @Column(name = "dropoff_location", nullable = false)
    private String dropoffLocation;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RideStatus status;

    @Column(name = "requested_at", nullable = false)
    private LocalDateTime requestedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;
}

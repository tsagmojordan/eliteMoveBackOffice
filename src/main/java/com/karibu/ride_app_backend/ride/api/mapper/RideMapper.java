package com.karibu.ride_app_backend.ride.api.mapper;

import com.karibu.ride_app_backend.ride.api.dto.CreateRideRequest;
import com.karibu.ride_app_backend.ride.api.dto.RideDto;
import com.karibu.ride_app_backend.ride.domain.model.Ride;

public class RideMapper {

    public static Ride toDomain(CreateRideRequest request) {
        return Ride.builder()
                .userId(request.getUserId())
                .vehiculeId(request.getVehiculeId())
                .pickupLocation(request.getPickupLocation())
                .dropoffLocation(request.getDropoffLocation())
                .build();
    }

    public static RideDto toDto(Ride ride) {
        return RideDto.builder()
                .id(ride.getId())
                .vehiculeId(ride.getVehiculeId())
                .userId(ride.getUserId())
                .pickupLocation(ride.getPickupLocation())
                .dropoffLocation(ride.getDropoffLocation())
                .status(ride.getStatus())
                .requestedAt(ride.getRequestedAt())
                .completedAt(ride.getCompletedAt())
                .build();
    }
}

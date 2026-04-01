package com.karibu.ride_app_backend.vehicule.api.mapper;

import com.karibu.ride_app_backend.vehicule.api.dto.CreateVehiculeRequest;
import com.karibu.ride_app_backend.vehicule.api.dto.VehiculeDto;
import com.karibu.ride_app_backend.vehicule.domain.model.Vehicule;

public class VehiculeMapper {

    public static Vehicule toDomain(CreateVehiculeRequest request) {
        return Vehicule.builder()
                .brand(request.getBrand())
                .model(request.getModel())
                .year(request.getYear())
                .licensePlate(request.getLicensePlate())
                .vehiculeClass(request.getVehiculeClass())
                .price(request.getPrice())
                .build();
    }

    public static VehiculeDto toDto(Vehicule vehicule) {
        return VehiculeDto.builder()
                .id(vehicule.getId())
                .brand(vehicule.getBrand())
                .model(vehicule.getModel())
                .year(vehicule.getYear())
                .licensePlate(vehicule.getLicensePlate())
                .vehiculeClass(vehicule.getVehiculeClass())
                .status(vehicule.getStatus())
                .price(vehicule.getPrice())
                .build();
    }
}

package com.karibu.ride_app_backend.vehicule.api.mapper;

import com.karibu.ride_app_backend.vehicule.api.dto.CreateVehiculeRequest;
import com.karibu.ride_app_backend.vehicule.api.dto.VehiculeDto;
import com.karibu.ride_app_backend.vehicule.application.helpers.FileManager;
import com.karibu.ride_app_backend.vehicule.domain.model.Vehicule;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class VehiculeMapper {

    private final FileManager fileManager;

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

    public VehiculeDto toDto(Vehicule vehicule) {
        return VehiculeDto.builder()
                .id(vehicule.getId())
                .brand(vehicule.getBrand())
                .model(vehicule.getModel())
                .year(vehicule.getYear())
                .licensePlate(vehicule.getLicensePlate())
                .vehiculeClass(vehicule.getVehiculeClass())
                .status(vehicule.getStatus())
                .price(vehicule.getPrice())
                .photo1(vehicule.getPhoto1())
                .photo2(vehicule.getPhoto2())
                .photo3(vehicule.getPhoto3())
                .photo1MimeType(vehicule.getPhoto1MimeType())
                .photo2MimeType(vehicule.getPhoto2MimeType())
                .photo3MimeType(vehicule.getPhoto3MimeType())
                .photo1ThumbnailPath(vehicule.getPhoto1ThumbnailPath())
                .build();
    }
}

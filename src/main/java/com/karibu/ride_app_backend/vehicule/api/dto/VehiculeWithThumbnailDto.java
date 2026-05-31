package com.karibu.ride_app_backend.vehicule.api.dto;

import com.karibu.ride_app_backend.vehicule.domain.model.VehiculeClass;
import com.karibu.ride_app_backend.vehicule.domain.model.VehiculeStatus;
import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class VehiculeWithThumbnailDto {
    private UUID id;
    private String brand;
    private String model;
    private int year;
    private String licensePlate;
    private VehiculeClass vehiculeClass;
    private VehiculeStatus status;
    private int price;
    private String photo1;
    private String photo2;
    private String photo3;
    private String photo1MimeType;
    private String photo1ThumbnailPath;
    private String thumbnailBase64;
}

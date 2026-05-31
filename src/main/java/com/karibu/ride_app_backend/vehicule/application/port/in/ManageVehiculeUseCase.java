package com.karibu.ride_app_backend.vehicule.application.port.in;

import com.karibu.ride_app_backend.vehicule.api.dto.VehiculePhotoResponse;
import com.karibu.ride_app_backend.vehicule.api.dto.VehiculeWithThumbnailDto;
import com.karibu.ride_app_backend.vehicule.domain.model.Vehicule;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ManageVehiculeUseCase {
    Vehicule createVehicule(Vehicule vehicule, List<MultipartFile> photos);

    Vehicule getVehicule(UUID id);

    List<Vehicule> getAllVehicules();

    List<Vehicule> getAvailableVehicules();

    Vehicule updateVehicule(UUID id, Vehicule vehicule);

    void deleteVehicule(UUID id);

    void updateVehiculeStatus(UUID id, String status);

    byte[] getVehiculeWithPrincipalImage(UUID id);

    VehiculePhotoResponse getPhoto1ByVehiculeId(UUID id);

    VehiculePhotoResponse getPhoto2ByVehiculeId(UUID id);

    VehiculePhotoResponse getPhoto3ByVehiculeId(UUID id);

    VehiculePhotoResponse getThumbnailByVehiculeId(UUID id);

    List<VehiculeWithThumbnailDto> getAllVehiculesWithThumbnails();

    List<VehiculeWithThumbnailDto> getAvailableVehiculesWithThumbnails();
}
